package zhengzhou.individual.interview.details;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailsPageAsyncTask extends AsyncTask<Void, Integer, Bitmap> {
    String imageUrl;
    DetailsPageAdatper.AdapterViewHolder holder;

    public DetailsPageAsyncTask(String imageUrl, DetailsPageAdatper.AdapterViewHolder holder) {
        this.imageUrl = imageUrl;
        this.holder = holder;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(20000);
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int length = -1;
                int progress = 0;
                int count = connection.getContentLength();
                byte[] bs = new byte[5];
                while ((length = is.read(bs)) != -1) {
                    progress += length;
                    if (count == 0) {
                        publishProgress(-1);
                    } else {
                        publishProgress((int) ((float) progress / count * 100));
                    }

                    if (isCancelled()) {
                        return null;
                    }
                    bos.write(bs, 0, length);
                }
                return BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        holder.getImageView().setImageBitmap(bitmap);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress != -1) {
            holder.getProgressBar().setProgress(progress);
        }
    }
}
