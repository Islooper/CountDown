package sample;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    @FXML
    private JFXButton begin;

    @FXML
    private JFXTextField hour;

    @FXML
    private JFXTextField min;

    @FXML
    void goHome(MouseEvent event) {
        // 判断时间是否设定
        String hour = this.hour.getText();
        String min = this.min.getText();

        if (hour.equals("") || min.equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setContentText("请输入下班时间");

            alert.showAndWait();
            return;
        }
        // 判断时间是否可用
        if (!checkTime(Integer.parseInt(hour) , Integer.parseInt(min))){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setContentText("请检查时间");

            alert.showAndWait();
            return;
        }

        // 获取相差的时间（秒数）
        int subSec = getSubTime(Integer.parseInt(hour) , Integer.parseInt(min));
        // 打开带有进度条的新窗口
        // 获取屏幕宽度、高度
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();

        // 创建新的stage
        Stage secondStage = new Stage();
        Pane pane = new Pane();
        JFXProgressBar jpb = new JFXProgressBar();
        jpb.setPrefWidth(width);
        jpb.setPrefHeight(10);
        pane.getChildren().addAll(jpb);
        Scene secondScene = new Scene(pane, width, 10);
        secondStage.setScene(secondScene);

        // 隐藏标题栏
        secondStage.initStyle(StageStyle.UNDECORATED);
        secondStage.setY(height - 80);
        secondStage.setAlwaysOnTop(true);
        secondStage.show();


        // 关闭当前窗口
        Stage stage = (Stage) begin.getScene().getWindow();
        stage.close();

        // 进度控制
        Thread thread = new Thread() {

            @Override
            public void run() {

                while (true){

                    if (sub >= subSec ){
                        Platform.runLater(() -> {
                            jpb.setProgress(1);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("柯以敏：");
                            alert.setContentText("下班了，滚吧");

                            alert.showAndWait();
                            // 关闭页面
                            Stage stage = (Stage) jpb.getScene().getWindow();
                            stage.close();
                        });
                        return;
                    }

                    sub += 1;
                    Platform.runLater(() -> {
                        double b = sub;
                        double a = subSec;
                        jpb.setProgress(b / a);

                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        };
        thread.setName("thread1");
        thread.start();


    }

    int sub = 0;
    private boolean checkTime(int hour ,int min) {
        // 获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        String now = df.format(new Date());
        String[] times = now.split(":");
        int hourNow = Integer.parseInt(times[0]);
        int minNow = Integer.parseInt(times[1]);

        if (hour < hourNow){
            return false;
        }
        if (hour == hourNow){
            if (min <= minNow){
                return false;
            }
        }
        return true;
    }

    private int getSubTime(int hour , int min){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        String now = df.format(new Date());
        String[] times = now.split(":");
        int hourNow = Integer.parseInt(times[0]);
        int minNow = Integer.parseInt(times[1]);

        int subHour = hour - hourNow;
        int subMin = min - minNow;

        // 全部转换为秒
       return subHour * 60 * 60 + subMin * 60;
    }
}
