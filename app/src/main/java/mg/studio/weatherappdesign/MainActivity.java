package mg.studio.weatherappdesign;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isNetworkConnected(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Internet unavailable", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(getApplicationContext(), "Freshing..", Toast.LENGTH_SHORT).show();
            new DownloadUpdate().execute();
        }
    }

    public void btnClick(View view) {
        Toast.makeText(getApplicationContext(), "正在更新天气...", Toast.LENGTH_SHORT).show();
        if (!isNetworkConnected(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "网络不可用，请稍后再试", Toast.LENGTH_SHORT).show();
        else
            new DownloadUpdate().execute();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {
        private final static String TAG = "DownloadUpdate";
        private String mAddressUrl = "http://api.openweathermap.org/data/2.5/forecast?q=Chongqing,cn&mode=json&APPID=aa3d744dc145ef9d350be4a80b16ecab";

        public DownloadUpdate() {
            super();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            try {
                URL url = new URL(mAddressUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                String jsonStr = buffer.toString();
                return parserJson(jsonStr);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        private String tempConvert(String Kelven) {
            float sourceF = Float.parseFloat(Kelven);
            sourceF = sourceF - 273+0.5f;
            int res = (int) sourceF;
            return String.valueOf(res);
        }
        @Override
        protected void onPostExecute(String source) {
            String info[] = source.split(",");
            change(info,1);
            ((TextView) findViewById(R.id.tv_date)).setText(getDayWeek(8));
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(tempConvert(info[0]));
            ((TextView) findViewById(R.id.first)).setText(getDayWeek(0));
            change(info,2);
            ((TextView) findViewById(R.id.second)).setText(getDayWeek(1));
            change(info,3);
            ((TextView) findViewById(R.id.third)).setText(getDayWeek(2));
            change(info,4);
            ((TextView) findViewById(R.id.fourth)).setText(getDayWeek(3));
            change(info,5);
            ((TextView) findViewById(R.id.fifth)).setText(getDayWeek(4));
            Toast.makeText(getApplicationContext(), "天气成功更新!", Toast.LENGTH_SHORT).show();
        }
        private String parserJson(String jsonStr) {
            String result = "";
            try {
                JSONObject obj = new JSONObject(jsonStr);
                JSONArray listArray = obj.getJSONArray("list");
                JSONObject list_item = listArray.getJSONObject(2);
                JSONArray weatherList = list_item.getJSONArray("weather");
                String mainweather1 = weatherList.getJSONObject(0).getString("main");
                String temp = list_item.getJSONObject("main").getString("temp");
                JSONObject list_item2 = listArray.getJSONObject(8);
                JSONArray weatherList2 = list_item2.getJSONArray("weather");
                String mainweather2 = weatherList2.getJSONObject(0).getString("main");
                JSONObject list_item3 = listArray.getJSONObject(16);
                JSONArray weatherList3 = list_item3.getJSONArray("weather");
                String mainweather3 = weatherList3.getJSONObject(0).getString("main");
                JSONObject list_item4 = listArray.getJSONObject(24);
                JSONArray weatherList4 = list_item4.getJSONArray("weather");
                String mainweather4 = weatherList4.getJSONObject(0).getString("main");
                JSONObject list_item5 = listArray.getJSONObject(32);
                JSONArray weatherList5 = list_item5.getJSONArray("weather");
                String mainweather5 = weatherList5.getJSONObject(0).getString("main");


                result = result+temp+","+mainweather1 + "," + mainweather2+","+mainweather3+","+mainweather4+","+mainweather5;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        private void change(String [] Info,int i){
            ImageView image;
            image= (ImageView)findViewById(R.id.img_first);
            if(i==2){
                image= (ImageView)findViewById(R.id.imgsecond);
            }
            if(i == 3){
                image= (ImageView)findViewById(R.id.imgthird);
            }
            if(i == 4){
                image= (ImageView)findViewById(R.id.imgfourth);
            }
            if(i == 4){
                image= (ImageView)findViewById(R.id.imgfourth);
            }
            if(i == 5){
                image= (ImageView)findViewById(R.id.imgfifth);
            }
            if (Info[i] == "Rain")
                image.setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else if (Info[i] == "Winds")
                image.setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            else if (Info[i] == "Clear")
                image.setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
            else
                image.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));
        }

        private String getDayWeek(int i){
            Calendar calendar=Calendar.getInstance();
            int week=calendar.get(Calendar.DAY_OF_WEEK);
            if(i == 8){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String day = new String();
                day=sdf.format(calendar.getTime());
                return day;
            }
            if(week == 7 && i != 0)
                week = 0;
            week = week + i;
            switch (week){
                case 1:
                    return "Sunday";
                case 2:
                    return "Monday";
                case 3:
                    return "Tuesday";
                case 4:
                    return "Wednesday";
                case 5:
                    return "Thursday";
                case 6:
                    return "Friday";
                case 7:
                    return "Saturday";
            }
            return null;
        }



    }

        public boolean isNetworkConnected(Context context) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
            return false;
        }

}
