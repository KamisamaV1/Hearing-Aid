# Hearing-Aid

1.	
Task: Creating Android Screens with Limited Functionality (using XML and Java).

Challenges/Hurdles: Major challenge was implementing BottomNavigationView Component.

Error: Constant Expression Required Error in Binding Different Fragments.

Fix: R.id using in the switch statement are declared as final or effectively final variables.
For example, if you're using a variable int sampleId = R.id.sample; as a case label, make sure it is declared as final int sampleId = R.id.sample;

Error: Cannot resolve symbol getItemId() method cannot be found or recognized for the menuItem.

Fix: Adding the import statement for the MenuItem class, import android.view.MenuItem;

Implementing below code,

ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
binding.bottomNavigationView.setOnItemSelectedListener((MenuItem menuItem) -> {
    int itemId = menuItem.getItemId();

Adding the import statement for the MenuItem class, import android.view.MenuItem;

Error: 	java.lang.NullPointerException: Can't toast on a thread that has not called Looper.prepare()

Fix: After registering the user on a separate thread, the runOnUiThread method is used to display the Toast message. This ensures that the UI-related operation (showing the Toast) is executed on the main thread, which is allowed to interact with the UI components.


2.	
Task: Creating DataBase and Table and Simultaneously Implementing the Functionality of Login/Register, View Profile, Forgot Password (Integrating XML, Java, Room Library (SQLite DataBase).

Challenges/Hurdles: Major challenge was implementing ViewProfile for Logged In User.

Error: 	java.lang.NullPointerException: Attempt to invoke virtual method 'void android.content.Intent.putExtra(java.lang.String, android.os.Bundle)' on a null object reference at com.example.LoginActivity.onCreate(LoginActivity.java:25)

LoginActivity

Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
mainIntent.putExtra("username", userName);
startActivity(mainIntent);
finish();

MainActivity
String username = mainActivity.getUsername();

Bundle args = new Bundle();
        args.putString("username", username);
        profileFragment.setArguments(args);

        String username = getArguments().getString("username");

Fix: SharedPreferences. 
During login, the username is stored in SharedPreferences, and during app usage, the stored username is retrieved from SharedPreferences.

SharedPreferences sharedPref = getSharedPreferences("user_shared_preferences", Context.MODE_PRIVATE);
SharedPreferences.Editor editor = sharedPref.edit();
editor.putString("loggedInUserName", userName);
editor.apply();

SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_shared_preferences", requireContext().MODE_PRIVATE);
username = sharedPref.getString("loggedInUserName", "");


Example of Minor Hurdles (Problem was Ignorance, Solution was Exploration).

•	java.lang.NullPointerException: Attempt to invoke virtual method 'android.text.Editable android.widget.EditText.getText()' on a null object reference
		Spinner gD;

Error:
	 user.setGender(gD. getText().toString());

Fix:
	user.setGender(gD.getSelectedItem().toString());
