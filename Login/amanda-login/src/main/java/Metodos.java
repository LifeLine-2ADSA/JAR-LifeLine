public class Metodos {
    String loginEmail;
    String loginSenha;
    Boolean cadastro(String nome, String email, String senha, String confirmarSenha, String telefone){
        Boolean cadastrado = false;
        if (senha.equals(confirmarSenha) && email != null && nome != null){
            cadastrado = true;
            loginEmail = email;
            loginSenha = senha;
        }
        return cadastrado;
    }

    Boolean login (String email, String senha){
        Boolean loginEfetuado = false;
        if(loginEmail == null || loginSenha == null){
            System.out.println("Faça o cadastro primeiro");

        }else if(email.equals(loginEmail) && senha.equals(loginSenha)){
            loginEfetuado = true;
        }
        return loginEfetuado;
    }

}
