package monzonation.aifamilybuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder>{

    private Context context;
    private ItemClickListener itemClickListener;
    List<IncomeModel> incomeModelList;

    public IncomeAdapter(Context context, List<IncomeModel> incomeModelList) {
        this.context = context;
        this.incomeModelList = incomeModelList;
    }
    @NonNull
    @Override
    public IncomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_list, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.ViewHolder holder, int position) {
        IncomeModel incomeModel = incomeModelList.get(position);
        String name = "from "+incomeModel.getName();
        String amount = "PHP "+incomeModel.getAmount();
        String date = "added on "+incomeModel.getDate();
        holder.textViewName.setText(name);
        holder.textViewAmount.setText(amount);
        holder.textViewDate.setText(date);
        holder.textViewDetails.setText(incomeModel.getDetails());
    }

    @Override
    public int getItemCount() {
        return incomeModelList.size();
    }
//    public IncomeModel getLastId() {
//        return incomeModelList.get(incomeModelList.size()-1);
//    }
    public IncomeModel getLastIdReversed() {
        return incomeModelList.get(0);
    }
    long getDataId(int id){
        IncomeModel incomeModel = incomeModelList.get(id);
        return incomeModel.getId();
    }
    String getDataName(int id){
        IncomeModel incomeModel = incomeModelList.get(id);
        return incomeModel.getName();
    }
    String getDataAmount(int id){
        IncomeModel incomeModel = incomeModelList.get(id);
        return incomeModel.getAmount();
    }
    String getDataDate(int id){
        IncomeModel incomeModel = incomeModelList.get(id);
        return incomeModel.getDate();
    }

    String getDataDetails(int id){
        IncomeModel incomeModel = incomeModelList.get(id);
        return incomeModel.getDetails();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewAmount, textViewDate, textViewDetails;
        ImageButton buttonDelete, buttonUpdate;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClickListener != null)
                        itemClickListener.onItemClickDelete(view, getAbsoluteAdapterPosition());
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClickListener != null)
                        itemClickListener.onItemClickUpdate(view, getAbsoluteAdapterPosition());
                }
            });

        }

    }
    void setClickListener(ItemClickListener itemClickListener2){
        this.itemClickListener = itemClickListener2;
    }

    public interface ItemClickListener {
        void onItemClickDelete(View view, int position);
        void onItemClickUpdate(View view, int position);
    }

}
