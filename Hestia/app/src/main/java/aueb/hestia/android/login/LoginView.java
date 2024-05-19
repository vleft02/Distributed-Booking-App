package aueb.hestia.android.login;



public interface LoginView {

    String ExtractUsername();
    void showErrorMessage(String title, String message);
}
