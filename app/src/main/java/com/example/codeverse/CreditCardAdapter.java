package com.example.codeverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.CreditCard;

import java.util.List;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CardViewHolder> {

    private List<CreditCard> cardsList;
    private Context context;
    private OnCardSelectedListener cardSelectedListener;
    private int selectedPosition = 0;

    public interface OnCardSelectedListener {
        void onCardSelected(CreditCard card, int position);
    }

    public CreditCardAdapter(Context context, List<CreditCard> cardsList, OnCardSelectedListener listener) {
        this.context = context;
        this.cardsList = cardsList;
        this.cardSelectedListener = listener;

        if (!cardsList.isEmpty()) {
            cardsList.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CreditCard card = cardsList.get(position);

        holder.tvCardNumber.setText(card.getFormattedCardNumber());
        holder.tvCardHolder.setText(card.getCardHolder());
        holder.tvCardExpiry.setText(card.getExpiryDate());
        holder.tvBankName.setText(card.getBankName());


        holder.ivCardBackground.setImageResource(card.getBackgroundResId());

        holder.ivCardNetwork.setImageResource(card.getCardTypeImage());

        holder.ivCardSelected.setVisibility(card.isSelected() ? View.VISIBLE : View.GONE);


        holder.itemView.setOnClickListener(v -> {

            if (selectedPosition != holder.getAdapterPosition()) {

                cardsList.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);

                selectedPosition = holder.getAdapterPosition();
                card.setSelected(true);
                notifyItemChanged(selectedPosition);

                if (cardSelectedListener != null) {
                    cardSelectedListener.onCardSelected(card, selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    public void updateCards(List<CreditCard> newCards) {
        this.cardsList = newCards;
        selectedPosition = 0;
        if (!newCards.isEmpty()) {
            newCards.get(0).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public CreditCard getSelectedCard() {
        if (!cardsList.isEmpty() && selectedPosition < cardsList.size()) {
            return cardsList.get(selectedPosition);
        }
        return null;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCardBackground;
        ImageView ivCardNetwork;
        ImageView ivCardSelected;
        TextView tvCardNumber;
        TextView tvCardHolder;
        TextView tvCardExpiry;
        TextView tvBankName;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCardBackground = itemView.findViewById(R.id.iv_card_bg);
            ivCardNetwork = itemView.findViewById(R.id.iv_card_network);
            ivCardSelected = itemView.findViewById(R.id.iv_card_selected);
            tvCardNumber = itemView.findViewById(R.id.tv_card_number);
            tvCardHolder = itemView.findViewById(R.id.tv_card_holder);
            tvCardExpiry = itemView.findViewById(R.id.tv_card_expiry);
            tvBankName = itemView.findViewById(R.id.tv_card_bank);
        }
    }
}