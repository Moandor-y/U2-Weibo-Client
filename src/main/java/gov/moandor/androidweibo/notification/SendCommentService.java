package gov.moandor.androidweibo.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import gov.moandor.androidweibo.R;
import gov.moandor.androidweibo.bean.CommentDraft;
import gov.moandor.androidweibo.concurrency.MyAsyncTask;
import gov.moandor.androidweibo.dao.BaseSendCommentDao;
import gov.moandor.androidweibo.dao.CreateCommentDao;
import gov.moandor.androidweibo.dao.ReplyCommentDao;
import gov.moandor.androidweibo.dao.RepostWeiboDao;
import gov.moandor.androidweibo.util.ActivityUtils;
import gov.moandor.androidweibo.util.DatabaseUtils;
import gov.moandor.androidweibo.util.GlobalContext;
import gov.moandor.androidweibo.util.Logger;
import gov.moandor.androidweibo.util.Utilities;
import gov.moandor.androidweibo.util.WeiboException;

public class SendCommentService extends Service {
    public static final String TOKEN = Utilities.buildIntentExtraName("TOKEN");
    public static final String COMMENT_DRAFT = Utilities.buildIntentExtraName("COMMENT_DRAFT");

    private NotificationManager mNotificationManager;
    private String mToken;
    private CommentDraft mDraft;

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mToken = intent.getStringExtra(TOKEN);
        mDraft = intent.getParcelableExtra(COMMENT_DRAFT);
        new SendTask().execute();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PendingIntent getFailedClickIntent() {
        return PendingIntent.getActivity(getBaseContext(), 0, ActivityUtils.draftBoxActivity(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private class SendTask extends MyAsyncTask<Void, Void, Void> {
        private int mNotificationId;

        @Override
        protected Void doInBackground(Void... v) {
            BaseSendCommentDao dao;
            if (mDraft.repliedComment == null) {
                dao = new CreateCommentDao();
            } else {
                dao = new ReplyCommentDao();
                ((ReplyCommentDao) dao).setCid(mDraft.repliedComment.id);
            }
            dao.setToken(mToken);
            dao.setComment(mDraft.content);
            dao.setId(mDraft.commentedStatus.id);
            dao.setCommentOri(mDraft.commentOri);
            try {
                dao.execute();
                if (mDraft.repostWhenComment) {
                    repost();
                }
            } catch (WeiboException e) {
                Logger.logException(e);
                mDraft.error = e.getMessage();
                DatabaseUtils.insertDraft(mDraft);
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setTicker(getString(R.string.sending));
            builder.setContentTitle(getString(R.string.sending));
            builder.setContentText(mDraft.content);
            builder.setOnlyAlertOnce(true);
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.ic_upload);
            builder.setProgress(0, 100, true);
            builder.setContentIntent(Utilities.newEmptyPendingIntent());
            mNotificationId = new Random().nextInt(Integer.MAX_VALUE);
            mNotificationManager.notify(mNotificationId, builder.build());
        }

        @Override
        protected void onPostExecute(Void result) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setTicker(getString(R.string.sent_successfully));
            builder.setContentTitle(getString(R.string.sent_successfully));
            builder.setOnlyAlertOnce(true);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.ic_accept);
            builder.setOngoing(false);
            builder.setContentIntent(Utilities.newEmptyPendingIntent());
            mNotificationManager.notify(mNotificationId, builder.build());
            GlobalContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNotificationManager.cancel(mNotificationId);
                    stopForeground(true);
                    stopSelf();
                }
            }, 3000);
        }

        @Override
        protected void onCancelled() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setTicker(getString(R.string.send_failed));
            builder.setContentTitle(getString(R.string.send_failed));
            builder.setContentText(mDraft.content);
            builder.setOnlyAlertOnce(true);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.ic_cancel);
            builder.setOngoing(false);
            builder.setContentIntent(getFailedClickIntent());
            mNotificationManager.notify(mNotificationId, builder.build());
            GlobalContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNotificationManager.cancel(mNotificationId);
                    stopForeground(true);
                    stopSelf();
                }
            }, 3000);
        }

        private void repost() throws WeiboException {
            RepostWeiboDao dao = new RepostWeiboDao();
            dao.setId(mDraft.commentedStatus.id);
            dao.setToken(mToken);
            dao.setStatus(mDraft.content);
            dao.execute();
        }
    }
}
