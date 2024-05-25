package aueb.hestia.android.room;

public interface RoomDetailsView {
    void showMessage(String message);

    void showDialog(String message);
    void onBackPressed();

    float getStarsroom();

    void hideReviewWidget();
    int getNoOfReviews();
}
