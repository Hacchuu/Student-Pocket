package com.story.mipsa.attendancetracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class subjectDialog extends AppCompatDialogFragment {

    public interface onInput{
        void sendInput(String input);
    }
    public onInput onInput;

    private EditText editText;
    private Button cancel,Add;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subject_dialog, container,false);
        editText = view.findViewById(R.id.enterSubject);
        cancel = view.findViewById(R.id.cancel);
        Add = view.findViewById(R.id.add);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subject = editText.getText().toString().trim();
                if(!subject.equals("")){
                    onInput.sendInput(subject);
                }
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    try {
            onInput = (onInput)getActivity();
    }catch (ClassCastException e){

    }
    }

}
