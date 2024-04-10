package com.login;

import javax.swing.*;
import java.util.Scanner;

public class SistemaLogin {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Sistema system = new Sistema();

        String escolha;

        do {
            JOptionPane.showMessageDialog(null, "Bem vindo(a), a Life Line!");
            escolha = JOptionPane.showInputDialog(null,"Escolha uma das opções abaixo:\n1 - Cadastro.\n2 - Login\n3 - Sair", "Life Line", JOptionPane.QUESTION_MESSAGE);

            switch (escolha) {
                case "1":
                    system.cadastroUsuario();
                     break;
                case "2":
                    system.logarUsuario();
                    break;
                case "3":
                    JOptionPane.showMessageDialog(null,"Obrigado por passar por aqui :)");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida");
            }
        } while (!escolha.equals("3"));
        JOptionPane.showMessageDialog(null, "Encerrando...");
    }
}
