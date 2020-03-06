package com.imaginology.texas.Routine.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.imaginology.texas.Dashboard.Teacher.RoutineDTO;
import com.imaginology.texas.R;
import com.imaginology.texas.ModifiedClasses.TableRowWithIndex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TableGenerator {
    private TableLayout tableLayout;
    private Context mContext;

    public TableGenerator(@NonNull Context mContext, @NonNull TableLayout tableLayout) {
        this.tableLayout = tableLayout;
        this.mContext = mContext;
    }

    public void createTableHeader(String[] tableHeaders) {
        TableRow trHeader = new TableRow(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            trHeader.setBackground(mContext.getResources().getDrawable(R.drawable.table_header_background));
        }else
            trHeader.setBackgroundColor(Color.GRAY);

        trHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        for (String tableHeader : tableHeaders) {
            TextView tvHeader = new TextView(mContext);
            tvHeader.setText(tableHeader);
            tvHeader.setTextColor(Color.WHITE);
            if(tableHeader.equalsIgnoreCase("day"))
                tvHeader.setGravity(Gravity.START);
            else
                tvHeader.setGravity(Gravity.CENTER_HORIZONTAL);

            tvHeader.setTextSize(18f);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf");
            tvHeader.setTypeface(typeface, Typeface.BOLD);
            tvHeader.setPadding(32, 20, 4, 20);

            tvHeader.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f
            ));
            trHeader.addView(tvHeader);// add the column to the table row here
        }
        tableLayout.addView(trHeader);
    }

    private Map<String, List<RoutineDTO>> getRoutineDeoGroupByDayOfWeek(List<RoutineDTO> routineList) {
        Map<String, List<RoutineDTO>> dayOfWeekListMap = new HashMap<>();

        if (routineList != null) {

            for (RoutineDTO dto : routineList) {
                if (!dayOfWeekListMap.containsKey(dto.getDay())) {
                    List<RoutineDTO> dtoList = new ArrayList<>();
                    dtoList.add(dto);
                    dayOfWeekListMap.put(dto.getDay(), dtoList);
                } else {
                    List<RoutineDTO> dtoList = dayOfWeekListMap.get(dto.getDay());
                    dtoList.add(dto);
                    dayOfWeekListMap.put(dto.getDay(), dtoList);
                }
            }

        }
        return dayOfWeekListMap;
    }

    public void createTableDataRows(List<RoutineDTO> routineList1) {
        Map<String, List<RoutineDTO>> routineListMap = getRoutineDeoGroupByDayOfWeek(routineList1);
        Map<Integer,List<RoutineDTO>>integerKeyRoutineListMap = new HashMap<>();

        for(Map.Entry<String,List<RoutineDTO>>entry: routineListMap.entrySet()){
            if(entry.getKey().equalsIgnoreCase("sunday")){
                integerKeyRoutineListMap.put(1,entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase("monday")){
                integerKeyRoutineListMap.put(2,entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase("tuesday")){
                integerKeyRoutineListMap.put(3,entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase("wednesday")){
                integerKeyRoutineListMap.put(4,entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase("thursday")){
                integerKeyRoutineListMap.put(5,entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase("friday")){
                integerKeyRoutineListMap.put(6,entry.getValue());
            }else if(entry.getKey().equalsIgnoreCase("saturday")){
                integerKeyRoutineListMap.put(7,entry.getValue());
            }
        }

        Map<Integer,List<RoutineDTO>> orderedHashmap= new TreeMap<>(integerKeyRoutineListMap);

        for (Map.Entry<Integer, List<RoutineDTO>> routineMap : orderedHashmap.entrySet()) {
            String dayOfWeek="";
            if(routineMap.getKey().equals(1)){
                dayOfWeek="Sunday";
            }else if(routineMap.getKey().equals(2)){
                dayOfWeek="Monday";
            } else if(routineMap.getKey().equals(3)){
                dayOfWeek="Tuesday";
            }
            else if(routineMap.getKey().equals(4)){
                dayOfWeek="Wednesday";
            }
            else if(routineMap.getKey().equals(5)){
                dayOfWeek="Thursday";
            }
            else if(routineMap.getKey().equals(6)){
                dayOfWeek="Friday";
            }else if(routineMap.getKey().equals(7)){
                dayOfWeek="Saturday";
            }


            List<RoutineDTO> routineList = routineMap.getValue();
            TableRowWithIndex trwiDay = new TableRowWithIndex(mContext);

            TextView textView = new TextView(mContext);
            textView.setText(dayOfWeek);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16f);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
            textView.setTypeface(typeface, Typeface.NORMAL);
            textView.setPadding(32, 8, 8, 16);
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textView.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f
            ));

            trwiDay.addView(textView);

            // Vertical linear layout to hold the redundant time views within same day
            LinearLayout tlTime = new TableLayout(mContext);
            tlTime.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f
            ));
            tlTime.setOrientation(LinearLayout.VERTICAL);

            // Vertical linear layout to hold the redundant subject views within same day
            LinearLayout tlSubject = new TableLayout(mContext);
            tlSubject.setOrientation(LinearLayout.VERTICAL);
            tlSubject.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f
            ));

            //Loop to fill the respective subjects and time views in their respective view holder
            for (RoutineDTO routine : routineList) {
                tlSubject.addView(getTextView(routine.getSubject()));
                tlTime.addView(getTextView(routine.getStartTime() + "-" + routine.getEndTime()));
            }


            trwiDay.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));


            trwiDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            trwiDay.addView(tlTime);
            trwiDay.addView(tlSubject);

            tableLayout.addView(trwiDay, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,                    //part4
                    TableLayout.LayoutParams.MATCH_PARENT));

            trwiDay.setRowIndex(tableLayout.indexOfChild(trwiDay));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Log.d("RowIndex: ",String.valueOf(trwiDay.getRowIndex()));
                if(trwiDay.getRowIndex()%2==0)
                    trwiDay.setBackground(mContext.getResources().getDrawable(R.drawable.even_table_row_background));
                else
                    trwiDay.setBackground(mContext.getResources().getDrawable(R.drawable.odd_table_row_background));

            } else {
                trwiDay.setBackgroundColor(Color.LTGRAY);
            }

        }

    }

    private View getTextView(String text) {
        TextView textView = new TextView(mContext);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16f);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
        textView.setTypeface(typeface, Typeface.NORMAL);
        textView.setPadding(32, 8, 8, 16);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return textView;
    }

}
