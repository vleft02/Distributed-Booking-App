package aueb.hestia.android.login;

import androidx.lifecycle.ViewModel;



public class LoginViewModel extends ViewModel{

    private LoginPresenter loginPresenter;
    /**
     * Αρχικοποιεί τον presenter και του περνάει ένα νέο αντικείμενο τύπου user dao memory, restaurant dao memory ,user dao memory
     * , owner dao memory για να χρησιμποιήσει
     */
    public LoginViewModel()
    {
        loginPresenter = new LoginPresenter();
    }
    /**
     * Επιστρέφει τον presenter στις κλάσεις όπου περιέχει τις πληροφορίες
     * @return το instance του presenter που δημιουργήσαμε παραπάνω
     */
    public LoginPresenter getPresenter() {
        return loginPresenter;
    }

}
