package com.example.hp.project4;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static android.R.attr.name;

public class MainActivity extends AppCompatActivity {

    Handler mHandler;
    Handler mWorkerHandler;
    Handler mWorkerHandler1;
    int totalgues;
    private volatile  boolean running=true;
    int restart_count=0;
    String feedback, feedback2;
    ArrayList<Integer> array=new ArrayList<Integer>();
    ArrayList<Integer> temp=new ArrayList<Integer>();
    Message m, m2;
    int rounds=0,numGuesses=0,guess=0;
    int flag1=0;
    int flag2=0;
    private Player1 t1;
    private Player2 t2;
    public int secret_sequence_1;
    public int guess_number_1;
    public int guess_number_2;
    public int secret_sequence_2;
    Runnable task2;
    Runnable task;
    private TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView7 = (TextView) findViewById(R.id.progressbar);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        t1 = new Player1("Player1");
        t2 = new Player2("Player2");

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("Message Received", "MainActivity");
                if (msg.arg1 == 1) {
                    textView7.setText("Status: Player1 wins");
                } else if (msg.arg1 == 2) {
                    textView7.setText("Status: Player2 wins");
                } else if (msg.arg1 == 3) {
                    textView7.setText("Status: No player wins- Tie");
                }
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restart_count > 0) {

                    running = false;
                    mWorkerHandler1.removeCallbacksAndMessages(null);
                    mWorkerHandler.removeCallbacksAndMessages(null);
                    Log.i("remove callbacks", "MainActivity");
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    running = true;
                    startActivity(i);


                } else {
                    task = new Runnable() {
                        @Override
                        public void run() {

                                restart_count++;
                                try {
                                    t1.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //secret_sequence_1 = ThreadLocalRandom.current().nextInt(1000, 9999);
                                if (flag1 == 0) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView7.setText("Status:Game In Progress");
                                        }
                                    });
                                    secret_sequence_1 = generate_random();
                                    Log.i("Secret seq 1 generated", "player 1");
                                    flag1++;
                                } else if (rounds < 21) {
                                    if (flag2 > 1) {
                                        feedback = check_number(guess_number_2, secret_sequence_1);
                                        String[] match_received = feedback2.split(":");
                                        int match_count=Integer.parseInt(match_received[0]);
                                        generate_list();
                                        updateMyGuess(match_count);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textView6.append(feedback);
                                                textView2.append(String.valueOf(guess_number_1 + "\n"));
                                            }
                                        });
                                        Log.i("feedback generated", "player 1");
                                        if (feedback == "Sequence equal") {
                                            Log.i("Player2 wins", "Player1");
                                            Message to_ui = Message.obtain();
                                            to_ui.arg1 = 2;
                                            mHandler.sendMessage(to_ui);
                                            //Add code for posting winner
                                        } else {
                                            guess_number_1 = myGuessIs();
                                            flag1++;
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.i("Display on 2 and 6", "player 1");
                                                   // textView6.append(feedback);
                                                   // textView2.append(String.valueOf(guess_number_1 + "\n"));
                                                    mWorkerHandler1.post(task2);
                                                    Log.i("Call thread 2", "player 1");
                                                }
                                            });
                                        }
                                    } else {
                                        generate_list();
                                        guess_number_1 = generate_random();
                                        flag1++;
                                        mWorkerHandler1.post(task2);

                                    }
                                } else {
                                    Log.i("Entering else", "T1");
                                    Message tie = Message.obtain();
                                    tie.arg1 = '3';
                                    mHandler.sendMessage(tie);
                                }
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (flag1 == 1)
                                            textView1.setText(String.valueOf(secret_sequence_1));
                                        else if (flag1 == 2) {
                                            textView2.append(String.valueOf(guess_number_1 + "\n"));
                                            mWorkerHandler1.post(task2);
                                        }
                                    }
                                });

                        }
                    };
                    task2 = new Runnable() {
                        @Override
                        public void run() {

                                try {
                                    t2.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (flag2 == 0) {
                                    secret_sequence_2 = generate_random();
                                    Log.i("Secret seq 2 generated", "Player2");
                                    flag2++;
                                } else if (rounds < 21) {
                                    if (flag1 > 1) {
                                        feedback2 = check_number(guess_number_1, secret_sequence_2);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textView3.append(feedback2);
                                                textView5.append(String.valueOf(guess_number_2 + "\n"));
                                            }
                                        });
                                        Log.i("Feedback2", "Player2");
                                        if (feedback2 == "Sequence equal") {
                                            Log.i("Player 1 wins", "Player2");
                                            Message to_ui1 = Message.obtain();
                                            to_ui1.arg1 = 1;
                                            mHandler.sendMessage(to_ui1);
                                        } else {
                                            //Log.i("Generating guess number 2 ", "Player2");
                                            guess_number_2 = generate_random();
                                            rounds++;
                                            flag2++;
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                   // Log.i("Displaying guess number 2 and feedback", "Player2");
                                                    //textView3.append(feedback2);
                                                    //textView5.append(String.valueOf(guess_number_2 + "\n"));
                                                    mWorkerHandler.post(task);
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView7.clearComposingText();
                                            textView7.setText("Tie");
                                        }
                                    });
                                }
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (flag2 == 1) {
                                            Log.i("Flag 2", "Player2");
                                            textView4.setText(String.valueOf(secret_sequence_2));
                                        }

                                        //textView5.append(String.valueOf(guess_number_2));

                                    }

                                });
                            }

                    };
                    t1.start();
                    t1.onLooperPrepared();
                    t2.start();
                    t2.onLooperPrepared();
                    t1.postTask(task);
                    t2.postTask(task2);
                    t1.postTask(task);
                }
            }
        });
    }

    public int myGuessIs() {
        //increases number of guesses
        Log.i("Entered myGuessIs","Player1");
        int tempIndex = (int) (Math.random() * array.size());
        Log.i("Generated tempIndex","Player1");
        String a=String.valueOf(tempIndex);
        Log.i("Player1",a);
        int myguess = array.get(tempIndex);
        //creates a new guess
        guess = myguess;
        //sets global variable
        return myguess;}

    @Override
    protected void onDestroy() {
        t1.quit();
        t2.quit();
        super.onDestroy();
    }

    public class Player1 extends HandlerThread{
        //    public Handler mWorkerHandler;
        public Player1(String name) {
            super(name);
        }
        public void postTask(Runnable task1){
            mWorkerHandler.post(task1);

        }

        @Override
        protected void onLooperPrepared() {
            mWorkerHandler = new Handler(getLooper()) {

                @Override
                public void handleMessage(Message m) {
                    Log.i("Entered handleMessage1", "MainActivity");
                    if(m.arg1==100)
                    {
                        Looper mylooper1=Looper.myLooper();
                        if(mylooper1!=null)
                            mylooper1.quit();
                    }

                }
            };
        }




    }

    public class Player2 extends HandlerThread{

        public Player2(String name)
        {
            super(name);
        }


        public void postTask(Runnable task){
            mWorkerHandler1.post(task);



        }
        @Override
        protected void onLooperPrepared(){
            mWorkerHandler1 = new Handler(getLooper()) {
                @Override
                public void handleMessage(Message m) {
                    Log.i("Entered handleMessage2", "MainActivity");
                    if (m.arg1 == 45) {
                        Looper mylooper = Looper.myLooper();
                        if (mylooper != null)
                            mylooper.quit();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView3.append(String.valueOf(m2.arg1));
                        }
                    });

                }

            };
        }
    }

    public void updateMyGuess(int nmatches) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        ArrayList<Integer> temp2 = new ArrayList<Integer>();
        int first = guess/1000;
        int second = (guess/100)%10;
        int third = (guess/10)%10;
        int fourth = guess%10;
        ArrayList<Integer> temp3 = new ArrayList<Integer>();
        temp3=array;
        //variables for each digit of guess
        if (nmatches == 1) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i)/1000 == first ||
                        (array.get(i)/100)%10 == second ||
                        (array.get(i)/10)%10 == third ||
                        array.get(i)%10 == fourth) temp.add(array.get(i));
            }
            array.clear();
            array.addAll(temp);
        }else if (nmatches == 2) {
            for (int i = 0; i < array.size(); i++) {
                if ((array.get(i)/1000 == first && (array.get(i)/100)%10 == second) ||
                        (array.get(i)/1000 == first && (array.get(i)/10)%10 == third)  ||
                        (array.get(i)/1000 == first &&  array.get(i)%10 == fourth) ||
                        ((array.get(i)/100)%10 == second && (array.get(i)/10)%10 == third) ||
                        ((array.get(i)/100)%10 == second && array.get(i)%10 == fourth) ||
                        (array.get(i)/10)%10 == third && array.get(i)%10 == fourth) temp.add(array.get(i));
            }
            array.clear();
            array.addAll(temp);
        }else if (nmatches == 3) {
            for (int i = 0; i < array.size(); i++) {
                if ((array.get(i)/1000 == first && (array.get(i)/100)%10 == second
                        && (array.get(i)/10)%10 == third) ||
                        (array.get(i)/1000 == first && (array.get(i)/10)%10 == third
                                && array.get(i)%10 == fourth) ||
                        ((array.get(i)/100)%10 == second && (array.get(i)/10)%10 == third
                                && array.get(i)%10 == fourth)) temp.add(array.get(i));
            }
            array.clear();
            array.addAll(temp);
        }else {
            for (int i = 0; i < array.size(); i++) {
                if ((array.get(i)/1000 == first) ||
                        ((array.get(i)/100)%10 == second) ||
                        ((array.get(i)/10)%10 == third) ||
                        (array.get(i)%10 == fourth)) temp2.add(array.get(i));
            }
            array.removeAll(temp2);
        }
        if(array.isEmpty())
        {
            array=temp3;
        }
        //creates new smaller ArrayList with better guesses
        //sets array as the smaller list
        //update the guess based on the number of matching digits claimed by the user
        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i) + "   " + i);
        } //troubleshooting

    }
    public void generate_list()
    {

        for(int i=1000;i<9999;i++){
            if(IsRepeating(i)){
                array.add(i);
            }
        }

    }

    synchronized public int generate_random(){
        int rand_no;
        do
        {
            rand_no = ThreadLocalRandom.current().nextInt(1000, 9999);
        } while(!IsRepeating(rand_no));

        return rand_no;
    }

    public static boolean IsRepeating(int rand_no)
    {
        int no = rand_no;
        Set<Integer> set = new HashSet<Integer>(); // HashSet contains only unique elements
        int count = 0;
        while (no > 0) {
            int tempVal = no % 10;
            set.add(tempVal);
            no = no / 10;
            count++;
        }
        if (count == set.size()){
            String test=String.valueOf(set.size());
            Log.i("set size",test);
            return true;
        }
        else
            return false;
    }

    synchronized public static String check_number(int guess_number,int secret_sequence){

        String result="null";
        int match=0;
        int match_incorrectpos=0;
        String g=Integer.toString(guess_number);
        char[] a=g.toCharArray();
        String s=Integer.toString(secret_sequence);
        char[] b=s.toCharArray();
        for(int i=0;i<4;i++)
        {
            if(a[i]==b[i]){
                match++;
            }
            for(int j=0;j<4;j++)
            {
                if(a[i]==b[j] && i!=j)
                {
                    match_incorrectpos++;
                }
            }
        }


        if(match==4)
        {
            result="Sequence equal";
        }
        else
        {
            result= match + ":" + match_incorrectpos + "\n";
        }

        return result;

    }
}

