package com.example.homework02;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("activity_ID---->"+Thread.currentThread().getId());
        //新建一个HanderThread对象，该对象实现了用Looper来处理消息队列的功能
        HandlerThread handler_thread = new HandlerThread("handler_thread");
        handler_thread.start();
        //MyHandler类是自己继承的一个类，这里采用hand_thread的Looper来初始化它
        MyHandler my_handler = new MyHandler(handler_thread.getLooper());
        //获得一个消息msg
        Message msg = my_handler.obtainMessage();

        //采用Bundle保存数据，Bundle中存放的是键值对的map，只是它的键值类型和数据类型比较固定而已
        Bundle b = new Bundle();
        b.putString("whether", "晴天");
        b.putInt("temperature", 34);
        msg.setData(b);
        //将msg发送到自己的handler中，这里指的是my_handler,调用该handler的HandleMessage方法来处理该mug
        msg.sendToTarget();
    }

    class MyHandler extends Handler
    {
        //空的构造函数
        public MyHandler()
        {}
        //以Looper类型参数传递的函数，Looper为消息泵，不断循环的从消息队列中得到消息并处理，因此
        //每个消息队列都有一个Looper，因为Looper是已经封装好了的消息队列和消息循环的类
        public MyHandler(Looper looper)
        {
            //调用父类的构造函数
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            System.out.println("Handler_ID---->"+Thread.currentThread().getId());
            System.out.println("Handler_Name---->"+Thread.currentThread().getId());
            //将消息中的bundle数据取出来
            Bundle b = msg.getData();
            String whether = b.getString("whether");
            int temperature = b.getInt("temperature");
            System.out.println("whether= "+whether+" ,temperature= "+temperature);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}