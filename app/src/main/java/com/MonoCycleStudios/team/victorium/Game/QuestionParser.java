package com.MonoCycleStudios.team.victorium.Game;

import android.content.Context;

import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;
import com.MonoCycleStudios.team.victorium.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionParser {

    private final static String question_filename = "questions.mm";
    private static int questionColvo = 0;

    public static void prepareQuestions(Context context){

        if(!context.getFileStreamPath(question_filename).exists()){// internal file is missing
            // than create file and write from base_questions
//            File file = new File(context.getFilesDir(), question_filename);

            InputStream s = context.getResources().openRawResource(R.raw.base_questions);

            BufferedInputStream bis = new BufferedInputStream(s, 1024);

            int i;

            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(question_filename, Context.MODE_PRIVATE);
                while((i = bis.read())!= -1){
                    outputStream.write(i);
                }
                outputStream.close();
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // than try to download new questions from server anyway

        String ss;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://victorium.mowemax.com/get-all-questions-raw.php").build();
        try (Response response = client.newCall(request).execute()) {

            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(question_filename, Context.MODE_PRIVATE);
                ss = response.body().string();
                Scanner scanner = new Scanner(ss);
                while (scanner.hasNextLine()) {
                    outputStream.write((scanner.nextLine()+"\n").getBytes());
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        questionColvo = QuestionParser.countLines(new File(context.getFilesDir(), question_filename));
        System.out.println("magic happen?"+questionColvo);
    }

    public static Object[] getRandomQuestion(){
        String s;

        s = getQuestionAtLine(new Random().nextInt(questionColvo-1)+1);

        return parse(s);
    }

    private static String getQuestionAtLine(int lineNumber){
        String ss = "";
        try(BufferedReader br = new BufferedReader(new FileReader(new File(Lobby.thisActivity.getApplicationContext().getFilesDir(), question_filename)))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                if(lineCount++ == lineNumber) {
                    ss = line;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ss;
    }

    private static int countLines(File input) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            int lineCount = 0;
            while (br.readLine() != null) {
                lineCount++;
            }
            return lineCount;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
}

    private static Object[] parse(String s){
        String[] que = s.split(":;");
        int rightAnsw = -1;

        System.arraycopy(shuffleAnswers(Arrays.copyOfRange(que, 2, 6)), 0, que, 2, 4);

        for(int i = 2; i <= 5; i++){
            if(que[i].startsWith("+")) {
                rightAnsw = i-2;
                que[i] = que[i].substring(1);
                break;
            }
        }

        return new Object[]{new Question(QuestionType.ONE_FROM_FOUR_NORMAL,
                QuestionCategory.getTypeOf(que[0].toLowerCase()),
                que[1],
                new String[]{que[2],que[3],que[4],que[5]},
                rightAnsw),
                rightAnsw
        };
    }

    private static String[] shuffleAnswers(String[] ans){

        Random rnd = new Random();  //Random rnd = ThreadLocalRandom.current();
        for (int i = ans.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ans[index];
            ans[index] = ans[i];
            ans[i] = a;
        }

        return ans;
    }
}
