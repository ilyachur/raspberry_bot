package com.home.CleverHome;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by ichuraev on 01/09/2015.
 */
public class MailWorkerAsync extends AsyncTask<Object, String, Boolean> {
    private Context context;
    private OnTaskCompleted listener;
    private String email_address;
    private String email_password;
    private String subtitle;
    private String bot_email_address;
    private String info;
    private String error = new String();

    MailWorkerAsync(Context context, OnTaskCompleted listener, String email_addr, String email_pwd, String bot_email, String subtitle) {
        this.context = context;
        this.listener = listener;
        email_address = email_addr;
        email_password = email_pwd;
        this.subtitle = subtitle;
        bot_email_address = bot_email;
    }

    /*
        ProgressDialog WaitingDialog;

        @Override
        protected void onPreExecute() {
            WaitingDialog = ProgressDialog.show(StatusActivity.this, "Отправка данных", "Отправляем сообщение...", true);
        }*/

        @Override
        protected void onPostExecute(Boolean result) {
            //WaitingDialog.dismiss();
            //Toast.makeText(context, "Отправка завершена!!!", Toast.LENGTH_LONG).show();
            if (!error.equals(""))
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            listener.onTaskCompleted(info);
        }

    @Override
    protected Boolean doInBackground(Object... params) {
        info = new String();
        error = "";
        MailWorker sender = new MailWorker(email_address, email_password);
        try {
            sender.sendMail(subtitle, "GET_STATUS=True", email_address, bot_email_address, "");
        } catch (Exception e) {
            error = context.getString(R.string.err_send_mail) + " " + e.toString();
            return false;
        }


        for (int i = 0; i < 12; i++) {
            try {
                info = sender.readMail(subtitle);
            } catch (Exception e) {
                error = context.getString(R.string.err_read_mail) + " " + e.toString();
                return false;
            }

            if (!info.equals(""))
                break;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
