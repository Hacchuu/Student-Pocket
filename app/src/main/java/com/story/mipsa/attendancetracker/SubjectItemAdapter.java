package com.story.mipsa.attendancetracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.progresviews.ProgressWheel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.VIBRATOR_SERVICE;


public class SubjectItemAdapter extends RecyclerView.Adapter<SubjectItemAdapter.ExampleViewHolder>  {
    private ArrayList<SubjectItem> subjectItems;
    private FragmentActivity context;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    private OnItemListener onItemListener;
    private boolean multiSelect = false;
    private ArrayList<SubjectItem> selectedItems = new ArrayList();
    MainActivity mainActivity;
    int flag;
    int extraFlag = 0;
    String extraStatus;
    SubjectDetails subjectDetails;
//    private static int extraClass = 0,countExtra=0;

    long currentDate;

    public int getExtraFlag() {
        return extraFlag;
    }

    public void setExtraFlag(int extraFlag) {
        this.extraFlag = extraFlag;
    }

    public String getExtraStatus() {
        return extraStatus;
    }

    public void setExtraStatus(String extraStatus) {
        this.extraStatus = extraStatus;
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            user = firebaseAuth.getCurrentUser();
            DatabaseReference userRef = ref.child("Users");
//            String sub = currentItem.getSubjectName();
           flag = 0;

            if(menuItem.getItemId() == R.id.action_delete){
                flag = 1;
                for(int i=0; i<selectedItems.size();i++){
                    subjectItems.remove(selectedItems.get(i));
                    String sub = selectedItems.get(i).getSubjectName();
                    userRef.child(user.getUid()).child("Subjects").child(sub).removeValue();
                }
                Toast.makeText(context,"Selected cards deleted",Toast.LENGTH_SHORT).show();
                actionMode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedItems.clear();
            if(flag == 1){
                context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                context.startActivity(new Intent(context,MainActivity.class));
                context.finish();
            }
            else {
                notifyDataSetChanged();
            }
        }
    };


    public SubjectItemAdapter(ArrayList<SubjectItem> exampleList, FragmentActivity context, OnItemListener onItemListener) {
        subjectItems = exampleList;
        this.context = context;
        this.onItemListener = onItemListener;
        mainActivity = (MainActivity) context;
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView subjectName, Attendance, Status;
        TextView optionDigit,displayExtra;
        ImageView addExtraImage;
        public Button present, absent;
        MainActivity mainActivity = new MainActivity();
        public int presentS, presentTemp = 0;
        public int absentS, total, totalS, attend = 0, bunk = 0, min, per;
        public float avg = 0, temp;
        private OnItemListener onItemListener;
        ProgressWheel progressWheelGreen;
        ProgressWheel progressWheelRed;



        //Custom view Holder that describes the items in the recycler view element
        public ExampleViewHolder(final View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            present = itemView.findViewById(R.id.item_present);
            absent = itemView.findViewById(R.id.item_absent);
            subjectName = itemView.findViewById(R.id.nameSubject);
            Attendance = itemView.findViewById(R.id.item_number);
            Status = itemView.findViewById(R.id.item_displayStatus);
//            optionDigit = itemView.findViewById(R.id.txtOptionDigit);
            progressWheelGreen = itemView.findViewById(R.id.wheelprogressGreen);
            progressWheelRed = itemView.findViewById(R.id.wheelprogressRed);
            displayExtra = itemView.findViewById(R.id.displayExtra);
            addExtraImage = itemView.findViewById(R.id.addExtra2);


            String target2 = "";

//            optionDigit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(),view);
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem menuItem) {
//                            switch (menuItem.getItemId()){
//                                case R.id.action_extra_class:
//                                    extraClass = 1;
//                                    countExtra += 1;
//                                    displayExtra.setVisibility(View.VISIBLE);
//                                    Toast.makeText(itemView.getContext(),"You have an extra class today", Toast.LENGTH_SHORT).show();
//                                    return true;
//                            }
//                            return false;
//                        }
//                    });
//                    popupMenu.inflate(R.menu.card_menu);
//                    popupMenu.show();
//                }
//            });

//            currentDate = mainActivity.getSelectedDate();


            String target = mainActivity.getMinimumAttendance();
            for (int i = 0; i < 3; i++) {
                if (target.charAt(i) == '%') {
                    break;
                } else {
                    target2 = target2 + target.charAt(i);
                }
            }
            min = Integer.parseInt(target2);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.OnItemClick(getAdapterPosition());
        }

//        @Override
//        public void sendExtraInput(String input, int position) {
//            Toast.makeText(context, "Gujju", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "Gujju", Toast.LENGTH_SHORT).show();
//            SubjectItem current = subjectItems.get(getAdapterPosition());
//            insertExtraClass(current, input);
//        }
//    }

//    private void insertExtraClass(SubjectItem current, String input) {
//        currentDate = mainActivity.getSelectedDate();
//        if (input.equalsIgnoreCase("Present")) {
//            current.setPresent(current.getPresent() + 1);
//            current.setTotal(current.getTotal() + 1);
//            current.setSubjectAttendanceDetails(new SubjectAttendanceDetails(input, currentDate, true));
//            subjectDetails.Recalculate();
//            mainActivity.buildRecyclerView();
//
//        } else if (input.equalsIgnoreCase("Absent")) {
//            current.setAbsent(current.getAbsent() + 1);
//            current.setTotal(current.getTotal() + 1);
//            current.setSubjectAttendanceDetails(new SubjectAttendanceDetails(input, currentDate, true));
//            subjectDetails.Recalculate();
//            mainActivity.buildRecyclerView();
//        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_example, parent, false);
        ExampleViewHolder exampleViewHolder = new ExampleViewHolder(v, onItemListener);
        return exampleViewHolder;
    }


    //This function determines which item in the list we are currently looking at
    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder holder, final int position) {
        holder.itemView.setAlpha(1f);
        final SubjectItem currentItem = subjectItems.get(position);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ref = database.getReference().getRoot();

        holder.addExtraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You clicked card", Toast.LENGTH_SHORT).show();
                ExtraClassDialog dialog = new ExtraClassDialog();
                Bundle bundle = new Bundle();
                bundle.putString("SubjectName", currentItem.getSubjectName());
                bundle.putInt("position", position);
                dialog.setArguments(bundle);
                dialog.show((context).getSupportFragmentManager(), "ExtraClassDialog");
            }
        });

        if(selectedItems.contains(currentItem)){
            holder.itemView.setAlpha(0.5f);
//            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
//        String stat = getExtraStatus();
//        int extra = getExtraFlag();
//        if(stat.equalsIgnoreCase("Present")){
//            Present(currentItem, holder, extra);
//        }
//        else if(stat.equalsIgnoreCase("Absent")){
//            Absent(currentItem, holder, extra);
//        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!multiSelect){
                    shakeItBaby();
                    multiSelect = true;
                    mainActivity.startActionMode(callback);
                    selectItems(holder,currentItem);
                }
                return true;
            }


        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(multiSelect){
                    shakeItBaby();
                    selectItems(holder,currentItem);
                }
                else{
                    onItemListener.OnItemClick(holder.getAdapterPosition());
                }
            }
        });

//        holder holds the pointer to the current item in the recycler view
        holder.subjectName.setText(currentItem.getSubjectName());
        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        if(currentItem.getPercentage() >= holder.min){
            holder.progressWheelRed.setVisibility(View.INVISIBLE);
            holder.progressWheelGreen.setVisibility(View.VISIBLE);
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        else{
            holder.progressWheelGreen.setVisibility(View.INVISIBLE);
            holder.progressWheelRed.setVisibility(View.VISIBLE);
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        if(currentItem.getTotal() != 0)
            Calculate(currentItem, holder);
        if (currentItem.getAttend() > 0) {
            if (currentItem.getAttend() > 1)
                holder.Status.setText("You can't bunk the next " + currentItem.getAttend() + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (currentItem.getBunk() > 0) {
            if (currentItem.getBunk() > 1)
                holder.Status.setText("You can bunk " + currentItem.getBunk() + " classes (¬‿¬)");
            else
                holder.Status.setText("You can bunk 1 class ^.^");
        }

        //When present button is pressed
        holder.present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Present(currentItem, holder);
                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                String sub = currentItem.getSubjectName();
                userRef.child(user.getUid()).child("Subjects").child(sub).setValue(currentItem);
            }
        });

        //When absent button is pressed
        holder.absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Absent(currentItem, holder);
                user = firebaseAuth.getCurrentUser();
                DatabaseReference userRef = ref.child("Users");
                String sub = currentItem.getSubjectName();
                userRef.child(user.getUid()).child("Subjects").child(sub).setValue(currentItem);
            }
        });
    }


    private void selectItems(ExampleViewHolder holder, SubjectItem currentItem) {
        if(selectedItems.contains(currentItem)){
            selectedItems.remove(currentItem);
            holder.itemView.setAlpha(1.0f);
        }
        else {
            selectedItems.add(currentItem);
            holder.itemView.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() {
        return subjectItems.size();
    }

    //Function to calculate all variables when present is enetered
    public void Present(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder ) {
        currentDate = mainActivity.getSelectedDate();
        ArrayList<SubjectAttendanceDetails> attendanceList = currentItem.getSubjectAttendanceDetails();
        int check = checkExisiting(attendanceList, currentDate, 0, currentItem);
        if(check == 1){
            return;
        }
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.presentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setPresent(holder.presentS);

//        if(extraClass == 1) {
//            setExtraFlag(0);
//            holder.displayExtra.setVisibility(View.INVISIBLE);
//            currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Present", currentDate,true));
//        }
//        else
            currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Present", currentDate,false));

        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        if(currentItem.getPercentage() >= holder.min){
            holder.progressWheelRed.setVisibility(View.INVISIBLE);
            holder.progressWheelGreen.setVisibility(View.VISIBLE);
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        else{
            holder.progressWheelGreen.setVisibility(View.INVISIBLE);
            holder.progressWheelRed.setVisibility(View.VISIBLE);
               holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        Calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.Status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.Status.setText("You can bunk " + holder.bunk + " classes (¬‿¬)");
            else if(holder.bunk == 1)
                holder.Status.setText("You can bunk 1 class ^.^");
        }
        }


    //Function to calculate all variables when absent is entered
    public void Absent(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
        currentDate = mainActivity.getSelectedDate();
        ArrayList<SubjectAttendanceDetails> attendanceList = currentItem.getSubjectAttendanceDetails();
        int check = checkExisiting(attendanceList, currentDate, 0, currentItem);
        if(check == 1)
            return;

        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.absentS++;
        holder.total++;
        float avg = ((float) holder.presentS / (float) holder.total) * 100;
        currentItem.setPercentage(avg);
        currentItem.setTotal(holder.total);
        currentItem.setAbsent(holder.absentS);
        holder.Attendance.setText(currentItem.getPresent() + "/" + currentItem.getTotal());
        if(currentItem.getPercentage() >= holder.min){
            holder.progressWheelRed.setVisibility(View.INVISIBLE);
            holder.progressWheelGreen.setVisibility(View.VISIBLE);
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }
        else{
            holder.progressWheelGreen.setVisibility(View.INVISIBLE);
            holder.progressWheelRed.setVisibility(View.VISIBLE);
            holder.progressWheelRed.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelGreen.setPercentage((int)(3.6 * currentItem.getPercentage()));
            holder.progressWheelRed.setStepCountText(String.format("%.1f%%", currentItem.getPercentage()));
        }

//        if(extraClass == 1) {
//            setExtraFlag(0);
//            holder.displayExtra.setVisibility(View.INVISIBLE);
//            currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Absent", currentDate,true));
//        }
//        else
            currentItem.setSubjectAttendanceDetails(new SubjectAttendanceDetails("Absent", currentDate,false));

        Calculate(currentItem, holder);
        if (holder.attend > 0) {
            if (holder.attend > 1)
                holder.Status.setText("You can't bunk the next " + holder.attend + " classes ⚆_⚆");
            else
                holder.Status.setText("You can't bunk the next class ◉_◉");
        } else if (holder.bunk > 0) {
            if (holder.bunk > 1)
                holder.Status.setText("You can bunk " + holder.bunk + " classes ♥‿♥");
            else if(holder.bunk == 1)
                holder.Status.setText("You can bunk your next class (ᵔᴥᵔ)");
        }
    }

    private int checkExisiting(ArrayList<SubjectAttendanceDetails> attendanceList, long currentDate, int extraClasss, SubjectItem currentItem) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        String currentDate1 = sdf.format(currentDate);
        for(int i=0; i<attendanceList.size();i++) {
            String checkDate = sdf.format(attendanceList.get(i).getDateOfEntry());
            boolean extraClass = attendanceList.get(i).isExtraClass();
            if(checkDate.equalsIgnoreCase(currentDate1)){
                if(extraClass){
                    return 0;
                }
                else{
                    Toast.makeText(mainActivity, "You have already entered the attendance for " + currentDate1, Toast.LENGTH_LONG).show();
                    return 1;
                }
//                if(extraClass == 0){
//                    Toast.makeText(mainActivity, "You have already entered the attendance for " + currentDate1, Toast.LENGTH_LONG).show();
//                    return 1;
//                }
//                else if(extraClass == 1){
//                    return 0;
//                }
            }
        }
        return 0;
    }

    //Calculate the prediction of number of classes to bunk or attend
    public void Calculate(SubjectItem currentItem, SubjectItemAdapter.ExampleViewHolder holder) {
        holder.absentS = currentItem.getAbsent();
        holder.presentS = currentItem.getPresent();
        holder.total = currentItem.getTotal();
        holder.totalS = holder.total;
        holder.attend = 0;
        holder.bunk = 0;
        if (holder.totalS != 0) {
            holder.avg = ((float) holder.presentS / (float) holder.totalS) * 100;
        }
        holder.temp = holder.avg;
        if (holder.temp >= holder.min) {
            do {
                holder.totalS += 1;
                holder.temp = ((float) holder.presentS / (float) holder.totalS) * 100;
                if (holder.temp < holder.min && holder.bunk == 0) {
                    holder.attend++;
                } else if (holder.temp >= holder.min && holder.attend == 0)
                    holder.bunk++;
            } while (holder.temp > holder.min);
        } else {
            holder.presentTemp = holder.presentS;
            do {
                holder.totalS += 1;
                holder.presentTemp += 1;
                holder.temp = ((float) holder.presentTemp / (float) holder.totalS) * 100;
                if (holder.temp <= holder.min && holder.bunk == 0) {
                    holder.attend++;
                } else if (holder.temp > holder.min && holder.attend == 0)
                    holder.bunk++;
            } while (holder.temp <= holder.min);
        }
        currentItem.setAttend(holder.attend);
        currentItem.setBunk(holder.bunk);
    }

    //Vibration on clikc method
    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(125);
        }
    }

    //Interface to send the position of the current item element
    public interface OnItemListener {
        void OnItemClick(int position);
    }


}


