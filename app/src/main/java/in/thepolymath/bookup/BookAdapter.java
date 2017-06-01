package in.thepolymath.bookup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static in.thepolymath.bookup.BookActivity.LOG_TAG;

/**
 * This is an adapter class for the Book object
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * This lets us connect to the view list_item
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book thisBook = getItem(position);

        String bookTitle, bookDesc, bookImage, bookDate, bookCost, bookAuthor, bookCategory;

        bookTitle = thisBook.getBookTitle();
        bookImage = thisBook.getBookImageLink();
        bookDesc = thisBook.getBookDesc();
        bookDate = thisBook.getBookDate();
        bookCost = thisBook.getBookCost();
        bookAuthor = thisBook.getBookAuthor();
        bookCategory = thisBook.getBookCategory();

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        try {
            Picasso.with(getContext()).load(bookImage).resize(180,260).into(imageView);
        }catch (Exception e){
            Log.e(LOG_TAG, "No image found.");
            TextView textView = (TextView) listItemView.findViewById(R.id.noimage);
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(bookTitle);

        TextView author = (TextView) listItemView.findViewById(R.id.author);
        if (bookAuthor.equals("")) {
            author.setText(R.string.no_author);
        } else author.setText(bookAuthor);

        TextView desc = (TextView) listItemView.findViewById(R.id.desc);
        if (bookDesc.equals("")) {
            desc.setText(R.string.no_desc);
        } else {
            if(bookDesc.length()>250) {
                String description = bookDesc.substring(0, 250);
                if(description.endsWith(" ")) {
                    description = description.substring(0,249) + "...";
                }
                else {
                    description = description+"...";
                }
                desc.setText(description);
            } else {
                desc.setText(bookDesc);
            }
        }

        TextView category = (TextView) listItemView.findViewById(R.id.category);
        if (bookCategory.equals("")) {
            category.setText(R.string.no_cat);
        } else category.setText(bookCategory);

        TextView date = (TextView) listItemView.findViewById(R.id.date);
        if (bookDate.equals("")) {
            date.setText(R.string.no_date);
        } else date.setText(bookDate);

        TextView cost = (TextView) listItemView.findViewById(R.id.cost);
        if (bookCost.equals("")) {
            cost.setText(R.string.no_cost);
        } else if (bookCost.equals(" ")) {
            cost.setText(R.string.no_cost);
        } else cost.setText(bookCost);

        return listItemView;
    }
}
