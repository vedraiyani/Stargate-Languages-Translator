package com.mou.stargatetranslator;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.view.View.*;
import android.content.*;
import android.graphics.*;
import android.provider.*;
import java.net.*;
import java.io.*;
import android.text.*;
import android.net.*;
import com.google.ads.*;
import android.database.*;
import java.math.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
	@Override
	private Bitmap getTransparentBitmapCopy(Bitmap source)
	{
		int width =  source.getWidth();
		int height = source.getHeight();
		Bitmap copy = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int[] pixels = new int[width * height];
		source.getPixels(pixels, 0, width, 0, 0, width, height);
		copy.setPixels(pixels, 0, width, 0, 0, width, height);
		return copy;
	}
	
	public void decodeImage(Uri uri){
		Typeface Ancient = Typeface.createFromAsset(getAssets(),"fonts/anquietas.ttf");
		Typeface Wraith = Typeface.createFromAsset(getAssets(),"fonts/wraith.ttf");
		Typeface Asgard = Typeface.createFromAsset(getAssets(),"fonts/asgard.ttf");
		Typeface Goauld1 = Typeface.createFromAsset(getAssets(),"fonts/goa_uld1.ttf");
		Typeface Furling = Typeface.createFromAsset(getAssets(),"fonts/Furling.ttf");
		Typeface Nox = Typeface.createFromAsset(getAssets(),"fonts/Nox.ttf");

		final Typeface[] Languages= {Ancient,Asgard,Wraith,Goauld1, Furling, Nox};
		final String[] LanguagesNames = {"Ancient","Asgard","Wraith","Goa'uld", "Furling","Nox"};
		final int[] LanguagesSize = {35, 23, 45, 27,38,25};

		final TextView output = (TextView) findViewById(R.id.output);
		final EditText input = (EditText) findViewById(R.id.input);

		String[] projection = {MediaStore.Images.Media.DESCRIPTION};
		try {
			Cursor c = MediaStore.Images.Media.query(getContentResolver(),uri,projection);
			c.moveToFirst();
			String data = c.getString(c.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
			String msg = data.split(";")[1];
			String lang = data.split(";")[0];
			output.setText(msg);
			for (int ix=0;ix<LanguagesNames.length;ix++){
				if (LanguagesNames[ix].equals(lang)){
					choice = ix;
					output.setTypeface(Languages[choice]);
					output.setTextSize(LanguagesSize[choice]);
				}
			}

			input.setText(msg);
		}catch(Exception e){
			input.setText(e.getMessage());
		}
	}
	
	final Context context = this;
	int choice = 0;
	boolean changed = false;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		final TextView output = (TextView) findViewById(R.id.output);
		final TextView hello = (TextView) findViewById(R.id.hello);
		final EditText input = (EditText) findViewById(R.id.input);
		
		final Button convert = (Button) findViewById(R.id.convert);
		Button showAlphabet = (Button) findViewById(R.id.showAlphabet);
		Button changeLangage = (Button) findViewById(R.id.changeLangage);
		
		Typeface Ancient = Typeface.createFromAsset(getAssets(),"fonts/anquietas.ttf");
		Typeface Wraith = Typeface.createFromAsset(getAssets(),"fonts/wraith.ttf");
		Typeface Asgard = Typeface.createFromAsset(getAssets(),"fonts/asgard.ttf");
		Typeface Goauld1 = Typeface.createFromAsset(getAssets(),"fonts/goa_uld1.ttf");
		Typeface Furling = Typeface.createFromAsset(getAssets(),"fonts/Furling.ttf");
		Typeface Nox = Typeface.createFromAsset(getAssets(),"fonts/Nox.ttf");
		
		final Typeface[] Languages= {Ancient,Asgard,Wraith,Goauld1, Furling, Nox};
		final String[] LanguagesNames = {"Ancient","Asgard","Wraith","Goa'uld", "Furling","Nox"};
		final int[] LanguagesSize = {35, 23, 45, 27,38,25};
		
		
		hello.setTextSize(15);
		output.setTextSize(35);
		
		output.setTypeface(Ancient);
		
		AdView adView = (AdView)this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		
		Intent intent = getIntent();
		
		if (intent.getData()!=null){
			decodeImage(intent.getData());
		}
		
		
		input.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s){
				if (!changed){
					changed = true;
					convert.setEnabled(true);
				}
				output.setText(input.getText().toString());
			}
			public void beforeTextChanged(CharSequence s,int start, int count, int after){}
			public void onTextChanged(CharSequence s,int start, int count, int after){}
		});
		
		changeLangage.setOnClickListener(new OnClickListener(){
			public void onClick(View p1){
				new AlertDialog.Builder(context)
				.setSingleChoiceItems(LanguagesNames,choice,null)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton){
						dialog.dismiss();
						int pos = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
						if (choice != pos){
							if (!changed){
								convert.setEnabled(true);
								changed = true;
							}
							choice = pos;
							output.setTypeface(Languages[choice]);
							output.setTextSize(LanguagesSize[choice]);
							Toast.makeText(getApplicationContext(),LanguagesNames[choice],Toast.LENGTH_SHORT).show();
						}
					}
				}).show();
			}
		});
		
		convert.setOnClickListener(new OnClickListener(){
			public void onClick(View p1){
				convert.setEnabled(false);
				changed = false;
				TextView glyphs = output;
				glyphs.setDrawingCacheEnabled(true);
				glyphs.destroyDrawingCache();
				Bitmap image = getTransparentBitmapCopy(glyphs.getDrawingCache());
				MediaStore.Images.Media.insertImage(getContentResolver(),image,input.getText().toString(),LanguagesNames[choice]+";"+input.getText().toString().replace(";",","));
				
				Toast.makeText(getApplicationContext(),"Saved glyphs to your gallery ready to share!",Toast.LENGTH_LONG).show();
			}
		});
		
		showAlphabet.setOnClickListener(new OnClickListener() {
			public void onClick(View p1){
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.alphabet);
				TextView facts = (TextView) dialog.findViewById(R.id.facts);
				ImageView image = (ImageView) dialog.findViewById(R.id.image);
				if (choice == 0){
					dialog.setTitle("Ancient Alphabet");
					facts.setText(getText(R.string.ancientfacts));
					image.setImageResource(R.drawable.ancientglyphs);
				}
				else if (choice == 1){
					dialog.setTitle("Asgard Alphabet");
					facts.setText(getText(R.string.asgardfacts));
					image.setImageResource(R.drawable.asgardalphabet);
				}
				else if (choice == 2){
					dialog.setTitle("Wraith Alphabet");
					facts.setText(getText(R.string.wraithfacts));
					image.setImageResource(R.drawable.wraith);
				}
				else if (choice == 3){
					dialog.setTitle("One of Goa'uld's Alphabet");
					facts.setText(getText(R.string.goauldfacts));
					image.setImageResource(R.drawable.goa_uld1);
				}
				else if (choice == 4){
					dialog.setTitle("Furling Alphabet");
					facts.setText(getText(R.string.furlingfacts));
					image.setImageResource(R.drawable.furling);
				}
				else{
					dialog.setTitle("Nox Alphabet");
					facts.setText(getText(R.string.noxfacts));
					image.setImageResource(R.drawable.nox);
				}
				Button ok = (Button) dialog.findViewById(R.id.dimiss);
				
				ok.setTypeface(output.getTypeface());
				ok.setTextSize(LanguagesSize[choice]);
				
				ok.setOnClickListener(new OnClickListener() {
					public void onClick(View v){
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.emailme:
			    Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"arnaudalies.py@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT,"Feedback of Stargate languages translator");
				try{
					startActivity(Intent.createChooser(i,"Send mail..."));
				}
				catch (android.content.ActivityNotFoundException e){
					Toast.makeText(getApplicationContext(),"no email client found...", Toast.LENGTH_SHORT).show();
				}
				return true;
			
			case R.id.rateit:
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.mou.stargatetranslator")));
				return true;
				
			case R.id.conv:
				//Uri uri = intent.getData();
				//Bitmap image = BitmapFactory.decodeFile(uri);
				Intent intent = new Intent();

				intent.setType("image/*");

				intent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
			return true;
				
			default:
			    return false;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent d) {
		if (resultCode == RESULT_OK) {
			decodeImage(d.getData());
		}
	}
}
