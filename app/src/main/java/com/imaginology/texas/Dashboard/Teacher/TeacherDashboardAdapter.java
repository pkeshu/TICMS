package com.imaginology.texas.Dashboard.Teacher;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.imaginology.texas.ClassRoutine.ClassRoutineDto;
import com.imaginology.texas.R;
import com.imaginology.texas.util.RecyclerviewItemAnimator;
import com.imaginology.texas.Routine.Util.TableGenerator;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardAdapter extends RecyclerView.Adapter<TeacherDashboardAdapter.TeacherViewHolder> {
    private Context mContext;
    private List<TeacherRoutineDTO> teacherRoutineDTOList=new ArrayList<>();
    private LayoutInflater inflater;
    private static final String[] TABLE_HEADERS = { "Day","Time","Subject"};
    final int COLUMN_WIDTH = 100;
    private int lastPosition = -1;

    public TeacherDashboardAdapter(Context mContext, List<TeacherRoutineDTO> teacherRoutineDTOList){
        this.mContext=mContext;
        this.teacherRoutineDTOList=teacherRoutineDTOList;
    }

    @Override
    public TeacherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_routine_row, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeacherViewHolder holder, int position) {
        holder.tvCourse.setText(teacherRoutineDTOList.get(position).getCourse());
        holder.tvSemester.setText(teacherRoutineDTOList.get(position).getSemester());

        TableGenerator tableGenerator= new TableGenerator(mContext,holder.tableLayout);
        tableGenerator.createTableHeader(TABLE_HEADERS);

        tableGenerator.createTableDataRows(teacherRoutineDTOList.get(position).getRoutinesList());

        lastPosition= RecyclerviewItemAnimator.setAnimation(mContext,holder.itemView, position, lastPosition);

    }

    @Override
    public int getItemCount() {
        return teacherRoutineDTOList.size();
    }

    public class TeacherViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView tvCourse;
        private TextView tvSemester;
//        private TableView<RoutineDTO> tvRoutine;
        private TableLayout tableLayout;

        public TeacherViewHolder(View itemView) {
            super(itemView);

            cardView= itemView.findViewById(R.id.cv_routine_holder);
            tvCourse= itemView.findViewById(R.id.tv_routine_course);
            tvSemester= itemView.findViewById(R.id.tv_routine_semester);
//            tvRoutine= itemView.findViewById(R.id.tv_routine_table);
            tableLayout= itemView.findViewById(R.id.tv_routine_table_custom);
        }
    }


}
