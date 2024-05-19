package aueb.hestia.android.login;


public class LoginPresenter {
    private LoginView view;

    public LoginView getView() {
        return view;
    }

    private String inputUsername;

    public void setView(LoginView view) {
        this.view = view;
    }

    public String authenticate() {
        inputUsername = view.ExtractUsername();
        if (inputUsername == null || inputUsername.equals(""))
        {
            view.showErrorMessage("Invalid Username", "Please give a valid username");
            return null;
        }
        else
        {
            return inputUsername;
        }
    }

}
