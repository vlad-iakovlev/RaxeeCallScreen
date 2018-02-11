package ru.raxee.call_screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

class RingingWindow {
    @SuppressLint("StaticFieldLeak")
    private static RingingWindow instance = null;

    private Context context;
    private View ringingView;
    private boolean isShown = false;


    private RingingWindow() {
        context = App.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        ringingView = inflater.inflate(R.layout.activity_ringing, null);


        FloatingActionButton answerButton = ringingView.findViewById(R.id.answer);
        answerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View buttonView) {
                answer();
            }
        });

        FloatingActionButton dismissButton = ringingView.findViewById(R.id.dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View buttonView) {
                dismiss();
            }
        });
    }

    static RingingWindow getInstance() {
        if (instance == null) {
            instance = new RingingWindow();
        }

        return instance;
    }


    void setData(Contact contact) {
        Resources resources = context.getResources();

        TextView name = ringingView.findViewById(R.id.name);
        TextView phone = ringingView.findViewById(R.id.phone);
        TextView company = ringingView.findViewById(R.id.company);
        ImageView photo = ringingView.findViewById(R.id.photo);

        name.setVisibility(View.GONE);
        phone.setVisibility(View.GONE);
        company.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);

        switch (contact.type) {
            case HIDDEN:
                name.setText(R.string.hidden_number);
                name.setVisibility(View.VISIBLE);
                break;

            case JUST_PHONE:
                name.setText(contact.number);
                name.setVisibility(View.VISIBLE);
                break;

            case FULL: {
                name.setText(contact.name);
                name.setVisibility(View.VISIBLE);

                phone.setText(contact.number);
                phone.setVisibility(View.VISIBLE);

                if (contact.company != null) {
                    if (contact.companyPosition != null) {
                        company.setText(resources.getString(R.string.full_company, contact.company, contact.companyPosition));
                        company.setVisibility(View.VISIBLE);
                    } else {
                        company.setText(contact.company);
                        company.setVisibility(View.VISIBLE);
                    }
                }

                if (contact.photo != null) {
                    photo.setImageBitmap(contact.photo);
                    photo.setVisibility(View.VISIBLE);
                }

                break;
            }
        }
    }

    void show() {
        if (!isShown) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity = Gravity.TOP | Gravity.START;

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            assert windowManager != null;
            windowManager.addView(ringingView, params);

            isShown = true;
        }
    }

    void hide() {
        if (isShown) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            assert windowManager != null;
            windowManager.removeView(ringingView);

            isShown = false;
        }
    }


    private void answer() {
        hide();

        Call call = Call.getInstance();
        call.answer();
    }

    private void dismiss() {
        hide();

        Call call = Call.getInstance();
        call.dismiss();
    }
}
