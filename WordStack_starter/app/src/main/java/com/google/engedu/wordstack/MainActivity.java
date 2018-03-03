package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;

    LinearLayout word1LinearLayout;
    LinearLayout word2LinearLayout;
    LinearLayout verticalLayout;

    private Stack<LetterTile> placedTiles = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = in.readLine()) != null) {
                String word = line.trim();

                if (word.length() == WORD_LENGTH)
                    words.add(word);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }


        verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);

        verticalLayout.addView(stackedLayout, 3); // Represents at which position to add the child.

        word1LinearLayout = (LinearLayout) findViewById(R.id.word1);
        word1LinearLayout.setOnTouchListener(new TouchListener());
        //word1LinearLayout.setOnDragListener(new DragListener());

        word2LinearLayout = (LinearLayout) findViewById(R.id.word2);
        word2LinearLayout.setOnTouchListener(new TouchListener());
        //word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }

                /*if (v == word1LinearLayout){
                    Toast.makeText(MainActivity.this, "word1", Toast.LENGTH_SHORT).show();
                }

                if (v == word2LinearLayout){
                    Toast.makeText(MainActivity.this, "word2", Toast.LENGTH_SHORT).show();
                }*/

                placedTiles.push(tile);

                /*
                ArrayList<Character> res = stackedLayout.iterate();

                for (Character c : res)
                    Toast.makeText(MainActivity.this, String.valueOf(c), Toast.LENGTH_SHORT).show();
                */
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }

                    placedTiles.push(tile);
                    /**
                     **
                     **  YOUR CODE GOES HERE
                     **
                     **/
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        word1LinearLayout.removeAllViews();
        word2LinearLayout.removeAllViews();
        stackedLayout.clear();


        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        String scrambledWord = new String();

        int position1 = random.nextInt(words.size());
        int position2 = random.nextInt(words.size());

        word1 = words.get(position1);
        word2 = words.get(position2);

        int count1 = 0;
        int count2 = 0;

        while (count1 < word1.length() && count2 < word2.length()) {
            int choice = random.nextInt(2);

            if (choice == 0) {
                scrambledWord += word1.charAt(count1);
                count1++;
            } else {
                scrambledWord += word2.charAt(count2);
                count2++;
            }
        }

        while (count1 < word1.length()) {
            scrambledWord += word1.charAt(count1);
            count1++;
        }

        while (count2 < word2.length()) {
            scrambledWord += word2.charAt(count2);
            count2++;
        }

        for (int i = scrambledWord.length() - 1; i >= 0; i--){
            LetterTile tile = new LetterTile(this, scrambledWord.charAt(i));
            stackedLayout.push(tile);
        }

        //messageBox.setText(scrambledWord + " " + word1 + " " + word2);

     /*   ArrayList<Character> res = stackedLayout.iterate();

        for (Character c : res)
            Toast.makeText(this, String.valueOf(c), Toast.LENGTH_SHORT).show();*/
        return true;
    }

    public boolean onUndo(View view) {

        LetterTile poppedTile = placedTiles.pop();
       // stackedLayout.push(poppedTile);

        poppedTile.moveToViewGroup(stackedLayout);

        return true;
    }
}
