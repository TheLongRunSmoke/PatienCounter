package com.tlrs.patiencounter;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeletDialog extends DialogFragment implements OnClickListener {

	private View rv;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Удаление последней записи");
		rv = inflater.inflate(R.layout.deletdialog, null);
		rv.findViewById(R.id.btnYes).setOnClickListener(this);
		return rv;
	}

	@Override
	public void onClick(View v) {
		if (((Button) v).getId() == R.id.btnYes) {
			String temp = ((EditText)rv.findViewById(R.id.delfield)).getText().toString();
			//Log.d("LOG", temp);
			if (temp.equalsIgnoreCase("Удалить")) {
				DBHelper dbHelper = new DBHelper(getActivity());
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				if (db.query("Stat",null,null,null,null,null,null).getCount() != 0){
				String lastrow = "_id = (SELECT MAX(_id) FROM Stat)";
				db.delete("Stat", lastrow, null);
				dbHelper.close();
				} else{
					Toast toast = Toast.makeText(this.getActivity(), "База пуста",
							Toast.LENGTH_SHORT);
					toast.show();
					dismiss();
				}
				Toast toast = Toast.makeText(this.getActivity(), "Удалено",
						Toast.LENGTH_SHORT);
				toast.show();
				dismiss();
			}
			else{
				Toast toast = Toast.makeText(this.getActivity(), "Неверный ввод, удаление не произведено",
						Toast.LENGTH_SHORT);
				toast.show();
				dismiss();
			}
		}
	}

	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
	}

}
