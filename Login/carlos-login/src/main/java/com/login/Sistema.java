package com.login;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Sistema {
    List<Usuario> usuarios = new ArrayList<>();
    void cadastroUsuario() {
        Usuario usuarioNovo = new Usuario();

        String nome = JOptionPane.showInputDialog(null, "Digite seu nome:", "Cadastro - Life Line", JOptionPane.QUESTION_MESSAGE);
        String email = JOptionPane.showInputDialog(null, "Digite seu email:", "Cadastro - Life Line", JOptionPane.QUESTION_MESSAGE);
        String senha = JOptionPane.showInputDialog(null, "Digite seu senha:", "Cadastro - Life Line", JOptionPane.QUESTION_MESSAGE);
        String confirmarSenha = JOptionPane.showInputDialog(null, "Confirme a senha:", "Cadastro - Life Line", JOptionPane.QUESTION_MESSAGE);

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            // verificando se os campos não estão nulos)
            JOptionPane.showMessageDialog(null, "Erro no cadastro!\nAlgum dos campos está vazio!");
        } else if (!senha.equals(confirmarSenha)) {
            // Revisando senha
            JOptionPane.showMessageDialog(null, "Erro no cadastro!\nSenhas diferentes!");
        } else if (!email.contains("@") && !email.endsWith(".com")) {
            // verificando email
            JOptionPane.showMessageDialog(null, "Erro no cadastro!\nEmail sem @ ou .com no final");
        } else {
            Boolean hasEmail = false;
            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i).getEmail().equals(email)) {
                    hasEmail = true;
                    JOptionPane.showMessageDialog(null, "Erro no cadastro!\nJá existe um usuário com esse email");
                }
            } // Verificando se existe email existente

            if (!hasEmail) { // Cadastro usuário
                usuarioNovo.setNome(nome);
                usuarioNovo.setEmail(email);
                usuarioNovo.setSenha(senha);
                usuarios.add(usuarioNovo);

                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!");
            }
        }
    }

    void logarUsuario() {
        String email = JOptionPane.showInputDialog(null, "Digite o email:", "Login - Life Line", JOptionPane.QUESTION_MESSAGE);
        String senha = JOptionPane.showInputDialog(null, "Digite a senha:", "Login - Life Line", JOptionPane.QUESTION_MESSAGE);

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equals(email) && usuarios.get(i).getSenha().equals(senha)) {
                JOptionPane.showMessageDialog(null, "Olá, " + usuarios.get(i).getNome());
            }
        }
    }
}
