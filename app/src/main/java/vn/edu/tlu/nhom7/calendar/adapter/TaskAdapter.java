package vn.edu.tlu.nhom7.calendar.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> mListTask;

    private IClickListener iClickListener;

    public interface IClickListener {
        void onClickShowItem(Task task);
        void onClickUpdateItem(Task task);
        void onClickDeleteItem(Task task);
    }

    public TaskAdapter(List<Task> mListTask, IClickListener iClickListener) {
        this.mListTask = mListTask;
        this.iClickListener = iClickListener;
    }

    public void setData(List<Task> list) {
        this.mListTask = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final Task task = mListTask.get(position);
        if (task == null) {
            return;
        }
        holder.tv_StartTime.setText(task.getStartTime());
        holder.tv_EndTime.setText(task.getEndTime());
        holder.tv_TaskName.setText(task.getTaskName());
        holder.tv_Detail.setText(task.getTaskDescription());

        switch (task.getColor().trim().toLowerCase()) {
            case "xanh dương":
                holder.tv_imageColor.setImageResource(R.drawable.ic_task_cl_blue);
                break;
            case "xanh lục":
                holder.tv_imageColor.setImageResource(R.drawable.ic_task_cl_green);
                break;
            case "đỏ":
                holder.tv_imageColor.setImageResource(R.drawable.ic_task_cl_red);
                break;
            default:
                holder.tv_imageColor.setImageResource(R.drawable.ic_task_cl_yellow);
                break;
        }

        holder.itemTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onClickShowItem(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListTask != null){
            return mListTask.size();
        }
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener  {
        private TextView tv_StartTime, tv_EndTime, tv_TaskName, tv_Detail;
        private ImageView tv_imageColor;
        private ImageButton imageButton;
        private LinearLayout itemTask;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_StartTime = itemView.findViewById(R.id.tv_StartTime);
            tv_EndTime = itemView.findViewById(R.id.tv_EndTime);
            tv_TaskName = itemView.findViewById(R.id.tv_TaskName);
            tv_Detail = itemView.findViewById(R.id.tv_Detail);
            tv_imageColor = itemView.findViewById(R.id.tv_imageColor);
            imageButton = itemView.findViewById(R.id.imageButton);
            itemTask = itemView.findViewById(R.id.itemTask);

            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.task_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            Task task = mListTask.get(position);
            if (item.getItemId() == R.id.action_popup_edit) {
                iClickListener.onClickUpdateItem(task);
                return true;
            } else if (item.getItemId() == R.id.action_popup_delete) {
                iClickListener.onClickDeleteItem(task);
                return true;
            }
            return false;
        }
    }
}