package telegramBot.notification;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramBot.bot.TelegramBot;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Time {
   private static Timer timer = new Timer();
   private static TelegramBot bot = new TelegramBot();

    private static String timeToMedicine(final int firstTime,final int secondTime){
        boolean morningTime = false;
        boolean eveningTime = false;
        String[] currentTime = Calendar.getInstance().toString().split(",");
        int index = 0;
        while(index!=currentTime.length){
            if(currentTime[index].equals("AM_PM=0")){
                morningTime = true;
            }
            else if(currentTime[index].equals("AM_PM=1")){
                eveningTime = true;
            }
        index++;}

        for(int i = 0; i < currentTime.length; i++){
            if((morningTime)&&(currentTime[i].substring(0, 5).equals("HOUR="))
                    &&currentTime[i+2].equals("MINUTE=2")){
                if(Integer.parseInt(currentTime[i].substring(5))==firstTime){
                    return "yes";}}
            if((eveningTime)&&(currentTime[i].substring(0, 5).
                    equals("HOUR="))&&currentTime[i+2].equals("MINUTE=2")){
                if(Integer.parseInt(currentTime[i].substring(5))==secondTime){
                    return "yes";}}}
        return "no";}

    public static void sendNotice(Update update, int rate, String command) throws Exception{
        boolean flag = false;
        if(command.equals("/start")){
            flag = true;}
        else if(command.equals("/stop")){
            flag = false;}
        else{throw new Exception("Неверная команда");}

        if(!flag){
            timer.cancel();}
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(timeToMedicine(7,8).equals("yes")){
                 send(update.getMessage().getChatId().toString(),"Татьяна, время для ваших таблеток. " +
                         "Спасибо, что бережете свое здоровье.");
                    System.out.println(true);
                }
            }};
        try{
        timer.schedule(task,0,rate);}
        catch(IllegalStateException e){
            System.out.println("Таймер отключен");
        }}

    private static void send(String chatId, String message){
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage =
                new org.telegram.telegrambots.meta.api.methods.send.SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);

        try{
            bot.execute(sendMessage);}
        catch (TelegramApiException e){e.printStackTrace();}
        }
    }

