package org.techtown.ebookbgm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PapagoAPI extends AppCompatActivity {

    EditText et_target;
    TextView textView;
    Button btn ;
    String getresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papago_api);

        textView = findViewById(R.id.textView);
        et_target = findViewById(R.id.et_target);
        btn = findViewById(R.id.btn);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Translate translate = new Translate();
                translate.execute(); //버튼 클릭시 ASYNC 사용



            }
        });

    }

    class Translate extends AsyncTask <String ,Void, String >{   //ASYNCTASK를 사용


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override

        protected String doInBackground(String... strings) {

            //////네이버 API

            String clientId = "VUvojadVo0GKid8tsZsw";     //애플리케이션 클라이언트 아이디값";
            String clientSecret = "ckhZz7Rr4T";      //애플리케이션 클라이언트 시크릿값";
            try {
                String text = URLEncoder.encode(et_target.getText().toString(), "UTF-8");  /// 번역할 문장 Edittext  입력

                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=ko&target=en&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                //        textView.setText(response.toString());
                getresult = response.toString();

                getresult = getresult.split("\"")[27];   //스플릿으로 번역된 결과값만 가져오기
                textView.setText(getresult); //  텍스트뷰에  SET해주기




            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }
    }
}
