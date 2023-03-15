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

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder>{

    private Context context;
    private ItemClickListener itemClickListener;
    List<BalanceModel> balanceModelList;

    public BalanceAdapter(Context context, List<BalanceModel> balanceModelList) {
        this.context = context;
        this.balanceModelList = balanceModelList;
    }
    @NonNull
    @Override
    public BalanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_list, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceAdapter.ViewHolder holder, int position) {
        BalanceModel balanceModel = balanceModelList.get(position);
        String name = balanceModel.getName();
        String balance = "PHP "+balanceModel.getBalance();
        String income = "PHP "+balanceModel.getIncome();
        String expense = "PHP "+balanceModel.getExpense();
        holder.textViewYearMonth.setText(name);
        holder.textViewIncome.setText(income);
        holder.textViewExpense.setText(expense);
        holder.textViewBalance.setText(balance);
    }

    @Override
    public int getItemCount() {
        return balanceModelList.size();
    }
//    public IncomeModel getLastId() {
//        return incomeModelList.get(incomeModelList.size()-1);
//    }
    public BalanceModel getLastIdReversed() {
        return balanceModelList.get(0);
    }
    String getDataId(int id){
        BalanceModel balanceModel = balanceModelList.get(id);
        return balanceModel.getId();
    }
    String getDataName(int id){
        BalanceModel balanceModel = balanceModelList.get(id);
        return balanceModel.getName();
    }
    String getDataDate(int id){
        BalanceModel balanceModel = balanceModelList.get(id);
        return balanceModel.getDate();
    }
    long getDataBalance(int id){
        BalanceModel balanceModel = balanceModelList.get(id);
        return balanceModel.getBalance();
    }

    long getDataIncome(int id){
        BalanceModel balanceModel = balanceModelList.get(id);
        return balanceModel.getIncome();
    }

    long getDataExpense(int id){
        BalanceModel balanceModel = balanceModelList.get(id);
        return balanceModel.getExpense();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewYearMonth, textViewIncome, textViewExpense, textViewBalance;
        ImageButton buttonExport;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textViewYearMonth = itemView.findViewById(R.id.textViewYearMonth);
            textViewIncome = itemView.findViewById(R.id.textViewIncome);
            textViewExpense = itemView.findViewById(R.id.textViewExpense);
            textViewBalance = itemView.findViewById(R.id.textViewBalance);
            buttonExport = itemView.findViewById(R.id.buttonExport);



            buttonExport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClickListener != null)
                        itemClickListener.onItemClickExport(view, getAbsoluteAdapterPosition());
                }
            });

        }

    }
    void setClickListener(ItemClickListener itemClickListener2){
        this.itemClickListener = itemClickListener2;
    }

    public interface ItemClickListener {
        void onItemClickExport(View view, int position);
    }

}
