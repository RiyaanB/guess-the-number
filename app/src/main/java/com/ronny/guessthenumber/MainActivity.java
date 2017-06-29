package com.ronny.guessthenumber;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ronny.guessthenumber.custom.Try;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Try> tries;
    EditText one, two, three, four;
    int[] HIDDEN;
    int tryNum;
    ListView lvTries;
    TryAdapter adapter;
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tryNum = 1;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_ets);
        one = (EditText) linearLayout.findViewById(R.id.et_A);
        two = (EditText) linearLayout.findViewById(R.id.et_B);
        three = (EditText) linearLayout.findViewById(R.id.et_C);
        four = (EditText) linearLayout.findViewById(R.id.et_D);
        initBoxEnd();
        tries = new ArrayList<Try>();
        loadRandom();
        lvTries = (ListView)findViewById(R.id.lv_tries);
        adapter = new TryAdapter(this, -1, tries);
        lvTries.setAdapter(adapter);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void loadRandom(){
        int[] digs = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        for(int i = 0; i < digs.length; i++){
            int one = (int)(Math.random()*digs.length);
            int two = (int)(Math.random()*digs.length);
            int temp = digs[one];
            digs[one] = digs[two];
            digs[two] = temp;
        }
        HIDDEN = new int[4];
        for(int i = 0; i < HIDDEN.length; i++)
            HIDDEN[i] = digs[i];
        System.out.println("Guess it: " + Arrays.toString(HIDDEN));
    }

    public void onGuess(View view){
        try {
            int[] inputs = new int[4];
            inputs[0] = Integer.parseInt(one.getText().toString());
            inputs[1] = Integer.parseInt(two.getText().toString());
            inputs[2] = Integer.parseInt(three.getText().toString());
            inputs[3] = Integer.parseInt(four.getText().toString());
            Try newTry = new Try(inputs, HIDDEN, tryNum++);
            boolean over = true;
            for (int i : newTry.result)
                over = i != 2 ? false : over;
            tries.add(newTry);
            adapter.notifyDataSetChanged();
            if (over) {
                TextView tvBig = (TextView) findViewById(R.id.number);
                tvBig.setText(Arrays.toString(HIDDEN));
                String playerStatus;
                if (tryNum < 4)
                    playerStatus = "A stroke of luck!!";
                else if (tryNum < 8)
                    playerStatus = "Wow! You're a pro!";
                else if (tryNum < 12)
                    playerStatus = "Nicely done!";
                else if (tryNum < 16)
                    playerStatus = "You won...";
                else
                    playerStatus = "Be faster next time! :'(";
                Toast.makeText(this, "Number of guesses: " + tryNum + "\n" + playerStatus, Toast.LENGTH_LONG).show();
                vibrator.vibrate(2000);
                tries.clear();
                tryNum = 1;
                tvBig.setText("Guess the Number!");
                onClear(null);
            } else
                vibrator.vibrate(100);
        } catch (NumberFormatException e){
            vibrator.vibrate(100);
            Toast.makeText(this, "Enter every digit before guessing!", Toast.LENGTH_SHORT).show();
        }
    }

    class ViewHolder{
        ImageView[] ivs;
        TextView tvGuess;
    }

    class TryAdapter extends ArrayAdapter<Try> {

        public TryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Try> tries) {
            super(context, resource, tries);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.list_item_try, null);
                holder = new ViewHolder();
                holder.ivs = new ImageView[4];
                holder.ivs[0] = (ImageView)convertView.findViewById(R.id.iv_one);
                holder.ivs[1] = (ImageView)convertView.findViewById(R.id.iv_two);
                holder.ivs[2] = (ImageView)convertView.findViewById(R.id.iv_three);
                holder.ivs[3] = (ImageView)convertView.findViewById(R.id.iv_four);
                holder.tvGuess = (TextView)convertView.findViewById(R.id.tv_id_and_try);
                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();
            Try current = tries.get(position);
            for(int i = 0; i < 4; i++){
                if(current.result[i] == 0)
                    holder.ivs[i].setImageResource(R.drawable.ic_clear_black_24dp);
                else if(current.result[i] == 1)
                    holder.ivs[i].setImageResource(R.drawable.ic_done_black_24dp);
                else
                    holder.ivs[i].setImageResource(R.drawable.ic_done_all_black_24dp);
            }
            holder.tvGuess.setText(current.num + ".  " + current.DIGITS[0] + current.DIGITS[1] + current.DIGITS[2] + current.DIGITS[3]);
            return convertView;
        }
    }

    public void onRestart(MenuItem mi){
        loadRandom();
        onClear(null);
        tries.clear();
        tryNum = 1;
        adapter.notifyDataSetChanged();
    }

    public void onGiveUp(MenuItem mi){
        tries.clear();
        onClear(null);
        tryNum = 1;
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Better luck next time!", Toast.LENGTH_LONG).show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tvBig = (TextView) findViewById(R.id.number);
                        tvBig.setText(Arrays.toString(HIDDEN));
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tvBig = (TextView) findViewById(R.id.number);
                        tvBig.setText("Guess the Number!");
                    }
                });
            }
        });
        thread.start();
        loadRandom();
    }

    public void initBoxEnd(){
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(one.getText().toString().length() == 1)
                    two.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(two.getText().toString().length() == 1)
                    three.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(three.getText().toString().length() == 1)
                    four.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void onClear(View view){
        one.setText("");
        two.setText("");
        three.setText("");
        four.setText("");
        one.requestFocus();
    }

    public void onHelp(MenuItem mi){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
