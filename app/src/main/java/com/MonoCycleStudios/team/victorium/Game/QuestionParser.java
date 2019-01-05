package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;

import java.util.Arrays;
import java.util.Random;

public class QuestionParser {

    private static String[][] allQuestions = {
            {
                    "None:;Привіт :) Так будуть виглядати справжні питання, зліва, замість мене, буде категорія, удачі...:;Окей:;Угу,  я і так знав:;А,  кто ето?:;\\0/"
            },
            {
                    "Math:;Скільки буде 2+2?:;NaN:;2:;0:;+4",
                    "Math:;Скільки буде 4+2?:;NaN:;8:;+6:;-2"
            },
            {
                    "History:;Хто першим ступив на поверхню Місяця?:;+Ніл Армстронг:;Луіс Армстронг:;Олександр Попов:;Джейсм Дін",
                    "History:;Коли почалась Перша світова війна?:;1814:;+1914:;1916:;1936"
            },
            {
                    "Film:;В якому році Леонардо Ді Капріо отримав оскар?:;2008:;Не отримав:;2012:;+2016"
            },
            {
                    "Geography:;Скільки материків на Землі?:;5:;7:;+6:;4"
            },
            {
                    "Sport:;Скільки раз Володимир Кличко, із 69 проведених поєдинків, програв?:;4:;2:;+5:;6"
            },
            {
                    "Music:;Хто одержав найбільшу кількість Греммі?:;Вуітні Хьюстон:;Каньє Вест:;Джейсм Хетфілд:;+Георг Шолті"
            },
            {
                    "Literature:;Як звали бога війни у грецбкій міфології?:;Аїд:;Марс:;+Арес:;Юпітер"
            },
            {
                    "Biology:;Хто запропонував модель ДНК?:;+Френсісом Кріком і Джеймсом Ватсоном:;Роберт Кох:;Йоган Мендель:;Луі Пастер"
            },
            {
                    "Meme:;Що буде в результаті? \n \'int a = 2 << 2;\':;4:;2:;NaN:;+8"
            }
    };

    public static Object[] getQuestion(String param){
        String s;
        int indCategory = 0;

        if (param.equalsIgnoreCase("random")){
            indCategory = new Random().nextInt(allQuestions.length-1)+1;
        }else{
            switch (QuestionCategory.getTypeOf(param.toLowerCase())) {
                case NONE: {
                    indCategory = 0;
                }
                break;
                case MATH: {
                    indCategory = 1;
                }
                break;
                case HISTORY: {
                    indCategory = 2;
                }
                break;
                case FILM: {
                    indCategory = 3;
                }
                break;
                case GEOGRAPHY: {
                    indCategory = 4;
                }
                break;
                case SPORT: {
                    indCategory = 5;
                }
                break;
                case MUSIC: {
                    indCategory = 6;
                }
                break;
                case LITERATURE: {
                    indCategory = 7;
                }
                break;
                case BIOLOGY: {
                    indCategory = 8;
                }
                break;
                case MEME: {
                    indCategory = 9;
                }
                break;
            }
        }

        s = allQuestions[indCategory][new Random().nextInt((allQuestions[indCategory].length))];

        return parse(s);
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
