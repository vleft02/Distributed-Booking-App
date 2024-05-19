package aueb.hestia.android.login;

import androidx.lifecycle.ViewModel;



public class LoginViewModel extends ViewModel{

    private LoginPresenter loginPresenter;

    public LoginViewModel()
    {
        loginPresenter = new LoginPresenter();
    }

    public LoginPresenter getPresenter() {
        return loginPresenter;
    }

}
