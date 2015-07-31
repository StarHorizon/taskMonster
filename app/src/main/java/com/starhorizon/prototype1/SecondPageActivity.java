package com.starhorizon.prototype1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SecondPageActivity extends ActionBarActivity {

    private TextView taskContentTextView;
    private TextView timerContentTextView;
    private Button finishButton;
    private Handler handler=new Handler();

    private void clearAllSharedPreferencesAndGoBack(){
        InitialPageActivity.SP_task=getSharedPreferences(InitialPageActivity.SP_string_data, 0);
        InitialPageActivity.SP_task.edit()
                .putString(InitialPageActivity.SP_string_taskName, "")
                .putString(InitialPageActivity.SP_string_taskDate, "")
                .putBoolean(InitialPageActivity.SP_string_taskStatus, false)
                .putBoolean(InitialPageActivity.SP_string_taskProcess, false)
                .commit();
        // �ե� Handler �� removeCallbacks ��k, �R�����C�������檺�u�{��H
        handler.removeCallbacks(updateTimerThread);
        // �e���Ĥ@�ӭ���
        Intent intent=new Intent(SecondPageActivity.this,InitialPageActivity.class);
        startActivity(intent);
        SecondPageActivity.this.finish();
    }

    private void gotoInitialPageIfNecessary(String time){
        // �ɶ���
        if(time.equals("00:00:00")){
            // �ե� Handler �� removeCallbacks ��k, �R�����C�������檺�u�{��H
            handler.removeCallbacks(updateTimerThread);
            // �x�s task ���A
            InitialPageActivity.taskDone=false;
            InitialPageActivity.taskSet=false;
            saveTaskStatusAndProcess();
            // �e���Ĥ@�ӭ���
            Intent intent=new Intent(SecondPageActivity.this,InitialPageActivity.class);
            startActivity(intent);
            SecondPageActivity.this.finish();
        }
    }

    private String computeTimeDifference(String inputTime){
        String[] timeArray;
        int inputSecond,inputMin,inputHour,inputTimeInSecond;
        int timeLimit,timeLimitInSecond,timeDifferenceInSecond;
        int outputSecond,outputMin,outputHour;
        String outputDate;
        String outputHourString="",outputMinString="",outputSecondString="";
        timeLimit=24;

        timeArray=inputTime.split(":");
        inputSecond=Integer.valueOf(timeArray[2]);
        inputMin=Integer.valueOf(timeArray[1]);
        inputHour=Integer.valueOf(timeArray[0]);

        inputTimeInSecond=inputHour*3600+inputMin*60+inputSecond;
        timeLimitInSecond=timeLimit*3600;
        timeDifferenceInSecond=timeLimitInSecond-inputTimeInSecond;

        outputHour=timeDifferenceInSecond/3600;
        if(outputHour<10) outputHourString=outputHourString.concat("0"+outputHour);
        else  outputHourString=Integer.toString(outputHour);

        outputMin=(timeDifferenceInSecond-outputHour*3600)/60;
        if(outputMin<10) outputMinString=outputMinString.concat("0"+outputMin);
        else  outputMinString=Integer.toString(outputMin);

        outputSecond=(timeDifferenceInSecond-outputHour*3600-outputMin*60);
        if(outputSecond<10) outputSecondString=outputSecondString.concat("0"+outputSecond);
        else  outputSecondString=Integer.toString(outputSecond);

        outputDate=outputHourString+":"+outputMinString+":"+outputSecondString;

        return outputDate;
    }

    private Runnable updateTimerThread=new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date currentDate = new Date(System.currentTimeMillis());
            String string=formatter.format(currentDate);
            //Log.d("Test:Current time", string);

            // �]�w�ù��W�˼Ʈɤ���ܪ��ɶ�
            String timerContent=computeTimeDifference(string);
            timerContentTextView.setText(timerContent);

            // �Y�ϥΪ̰��d�b�������A�h���ȧ���������ɭn���^����
            gotoInitialPageIfNecessary(string);

            // �N�n���檺�u�{��H updateTimerThread ��J���C��, �����ݮɶ� (1000�@��)������, ����u�{��H updateTimerThread
            handler.postDelayed(this,1000);
        }
    };

    private void startRunProcess(){
        // �ե� Handler �� postDelayed ��k
        // �N�n���檺�u�{��H updateTimerThread ��J���C��, �����ݮɶ� (1000�@��)������, ����u�{��H updateTimerThread
        handler.postDelayed(updateTimerThread,1000);
    }

    private void saveTaskStatusAndProcess(){
        InitialPageActivity.SP_task=getSharedPreferences(InitialPageActivity.SP_string_data,0);
        InitialPageActivity.SP_task.edit()
                .putBoolean(InitialPageActivity.SP_string_taskStatus, InitialPageActivity.taskSet)
                .putBoolean(InitialPageActivity.SP_string_taskProcess, InitialPageActivity.taskDone)
                .commit();
    }

    private String readTaskName(){
        InitialPageActivity.SP_task=getSharedPreferences(InitialPageActivity.SP_string_data,0);
        return InitialPageActivity.SP_task.getString(InitialPageActivity.SP_string_taskName,"");
    }

    private void setButtons(){
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // �ե� Handler �� removeCallbacks ��k, �R�����C�������檺�u�{��H
                handler.removeCallbacks(updateTimerThread);
                // �x�s task ���A
                InitialPageActivity.taskSet=true;
                InitialPageActivity.taskDone=true;
                saveTaskStatusAndProcess();
                // �e���ĤT�ӭ���
                Intent intent=new Intent(SecondPageActivity.this,ThirdPageActivity.class);
                startActivity(intent);
                SecondPageActivity.this.finish();
            }
        });
    }

    private void findViews(){
        taskContentTextView=(TextView)findViewById(R.id.textView);
        timerContentTextView=(TextView)findViewById(R.id.textView2);
        finishButton=(Button)findViewById(R.id.button2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        //�Ұʭ˼Ƴq��
        Intent serviceIntent = new Intent(SecondPageActivity.this, CountDown.class);
        startService(serviceIntent);

        findViews();
        setButtons();

        // ��ܥ��Ȥ��e
        String taskName=readTaskName();
        taskContentTextView.setText(taskName);

        // �}�l��s������ܪ��˼Ʈɶ� process
        startRunProcess();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // �ե� Handler �� removeCallbacks ��k, �R�����C�������檺�u�{��H
        handler.removeCallbacks(updateTimerThread);
        SecondPageActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            clearAllSharedPreferencesAndGoBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}