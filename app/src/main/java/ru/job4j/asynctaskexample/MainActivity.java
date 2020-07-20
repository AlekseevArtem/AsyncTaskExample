package ru.job4j.asynctaskexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private SampleAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onDestroy() {
        task = null;
        super.onDestroy();
    }

    public void startAsyncTask(View view) {
        task = new SampleAsyncTask(this);
        task.execute(10);
    }

    private static class SampleAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<MainActivity> mActivityWeakReference;

        SampleAsyncTask(MainActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            int count = 0;
            while (count < integers[0]) {
                publishProgress((count * 100)/integers[0]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
            return "Finish";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = mActivityWeakReference.get();
            if(activity == null || activity.isFinishing()) {
                return;
            }
            activity.mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MainActivity activity = mActivityWeakReference.get();
            if(activity == null || activity.isFinishing()) {
                return;
            }
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.mProgressBar.setProgress(0);
            activity.mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = mActivityWeakReference.get();
            if(activity == null || activity.isFinishing()) {
                return;
            }
            activity.mProgressBar.setProgress(values[0]);
        }
    }
}