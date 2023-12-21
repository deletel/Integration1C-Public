package org.example;

import org.json.JSONArray;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainSwing {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String POST_URL = "http://1cerpsrv.meteor.lift/do_test_2/hs/buy/";
    static String[] companies;

    public static void main(String[] args)  {

        LoginWindow jFrame = new LoginWindow();
        jFrame.setVisible(true);
        jFrame.setBounds(750, 250, 300,220);

    }

    public static class LoginWindow extends JFrame {

        static JComboBox company;
        static JTextField product;
        static JTextField quantity;
        static JLabel statusLabel;
        
        LoginWindow() {
            super("Заказ Поставщику");
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            Box box1 = Box.createHorizontalBox();
            companies = new String[]{
                    "Новая компания",
                    "Старая компания",
                    "Метеор лифт"
            };

            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox box = (JComboBox)e.getSource();
                    String item1 = (String)box.getSelectedItem();
                }
            };

            company = new JComboBox(companies);

            JLabel loginLabel = new JLabel("Контрагент:");
            box1.add(loginLabel);
            company.addActionListener(actionListener);
            box1.add(Box.createHorizontalStrut(34));
            box1.add(company);

            Box box2 = Box.createHorizontalBox();
            JLabel passwordLabel = new JLabel("Товар:");
            product = new JTextField(20);
            box2.add(passwordLabel);
            box2.add(Box.createHorizontalStrut(40));
            box2.add(product);

            Box box3 = Box.createHorizontalBox();
            JLabel qLabel = new JLabel("Количество:");
            quantity = new JTextField(20);
            box3.add(qLabel);
            box3.add(Box.createHorizontalStrut(6));
            box3.add(quantity);

            Box box4 = Box.createHorizontalBox();
            JButton okButton = new JButton("Отправить");
            //okButton.setActionCommand("OK");
            okButton.addActionListener(new ButtonClickListener());
            box4.add(okButton);

            Box box5 = Box.createHorizontalBox();
            statusLabel = new JLabel("", JLabel.CENTER);

            box5.add(statusLabel);

            loginLabel.setPreferredSize(passwordLabel.getPreferredSize());
            Box mainBox = Box.createVerticalBox();
            mainBox.setBorder(new EmptyBorder(12, 12, 12, 12));
            mainBox.add(box1);
            mainBox.add(Box.createVerticalStrut(12));
            mainBox.add(box2);
            mainBox.add(Box.createVerticalStrut(12));
            mainBox.add(box3);
            mainBox.add(Box.createVerticalStrut(12));
            mainBox.add(box4);
            mainBox.add(Box.createVerticalStrut(10));
            mainBox.add(box5);
            setContentPane(mainBox);
            pack();
            setResizable(false);
        }
        //}

        public static class ButtonClickListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                String[] delive = {companies[company.getSelectedIndex()], product.getText(), quantity.getText()};
                try {
                    statusLabel.setText(PostRequest(delive));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }

            public static String PostRequest(String[] delive) throws Exception {

                URL obj = new URL(POST_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept", "application/json");
                JSONArray jsonDelive = new JSONArray(delive);

                String authString = System.getenv("login") + ":" + System.getenv("password");
                String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
                String authHeader = "Basic " + encodedAuth;
                con.setRequestProperty("Authorization", authHeader);

                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonDelive.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                }

                StringBuilder response = new StringBuilder();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
                return response.toString();
            }

        }
    }
}
