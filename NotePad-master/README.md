# NOTEPAD基础功能实现及扩展

## 1.基础功能：主页笔记列表中增加相应笔记时间戳显示

原有的NotePad中主页列表只显示了笔记的标题而没有其它的识别标记，为了更好地找到所需要的笔记，可以为每个笔记下面添加时间戳
通过编辑note_list.xml文件，在原先的TextView，即笔记的标题下面再添加一个TextView，并设置为线性垂直布局，让时间戳在笔记标题的下方

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:paddingBottom="3dip">

    <TextView
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="?android:attr/listPreferredItemHeight"
        android:textAppearance="?android:attr/textAppearanceLarge"
        android:gravity="center_vertical"
        android:singleLine="true"
        />

    <TextView
        android:id="@android:id/text2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:singleLine="true"
        android:textAppearance="?android:attr/textAppearanceLarge" />
</LinearLayout>
```

在NotePadProvider.java中查看数据库结构：

```java
@Override
public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + "   ("
            + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
            + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
            + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
            + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
            + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER,"
            + ");");
}
```

再到NoteList.java文件中查看如何将数据装填到列表中。数据被定义**在PROJECTION**中,于是使用修改时间作为显示的时间

```java
private static final String[] PROJECTION = new String[] {
        NotePad.Notes._ID, // 0
        NotePad.Notes.COLUMN_NAME_TITLE, // 1
        NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,//修改时间
};
```

通过Cursor读出

```java
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
```

通过SimpleCursorAdapter装填
```java
// Creates the backing adapter for the ListView.
SimpleCursorAdapter adapter
    = new SimpleCursorAdapter(
              this,                             // The Context for the ListView
              R.layout.noteslist_item,          // Points to the XML for a list item
              cursor,                           // The cursor to get items from
              dataColumns,
              viewIDs
      );

// Sets the ListView's adapter to be the cursor adapter that was just created.
setListAdapter(adapter);
```

插入时间，在NotePadProvider.java中的insert方法和NoteEditor.java中的updateNote方法中实现，前者为创建笔记的时间，后者为修改笔记更新的时间
```java
    public Uri insert(Uri uri, ContentValues initialValues) {

        // Validates the incoming URI. Only the full provider URI is allowed for inserts.
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // A map to hold the new record's values.
        ContentValues values ;

        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }

        // Gets the current system time in milliseconds
        Long now = Long.valueOf(System.currentTimeMillis());

        // If the values map doesn't contain the creation date, sets the value to the current time.
        if (values.containsKey(NotePad.Notes.COLUMN_NAME_CREATE_DATE) == false) {
            values.put(NotePad.Notes.COLUMN_NAME_CREATE_DATE, now);
        }

        // If the values map doesn't contain the modification date, sets the value to the current
        // time.
        if (values.containsKey(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE) == false) {
            values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, now);
        }

        // If the values map doesn't contain a title, sets the value to the default title.
        if (values.containsKey(NotePad.Notes.COLUMN_NAME_TITLE) == false) {
            Resources r = Resources.getSystem();
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, r.getString(android.R.string.untitled));
        }

        // If the values map doesn't contain note text, sets the value to an empty string.
        if (values.containsKey(NotePad.Notes.COLUMN_NAME_NOTE) == false) {
            values.put(NotePad.Notes.COLUMN_NAME_NOTE, "");
        }
        values.put(NotePad.Notes.COLUMN_NAME_TIMESTAMP, now);

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(
            NotePad.Notes.TABLE_NAME,        // The table to insert into.
            null,  // A hack, SQLite sets this column value to null
                                             // if values is empty.
            values                           // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(NotePad.Notes.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
    }
```
```java
    private final void updateNote(String text, String title) {

        // Sets up a map to contain values to be updated in the provider.
        ContentValues values = new ContentValues();
        values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());

        // If the action is to insert a new note, this creates an initial title for it.
        if (mState == STATE_INSERT) {

            // If no title was provided as an argument, create one from the note text.
            if (title == null) {
  
                // Get the note's length
                int length = text.length();

                // Sets the title by getting a substring of the text that is 31 characters long
                // or the number of characters in the note plus one, whichever is smaller.
                title = text.substring(0, Math.min(30, length));
  
                // If the resulting length is more than 30 characters, chops off any
                // trailing spaces
                if (length > 30) {
                    int lastSpace = title.lastIndexOf(' ');
                    if (lastSpace > 0) {
                        title = title.substring(0, lastSpace);
                    }
                }
            }
            // In the values map, sets the value of the title
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
        } else if (title != null) {
            // In the values map, sets the value of the title
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
        }

        // This puts the desired notes text into the map.
        values.put(NotePad.Notes.COLUMN_NAME_NOTE, text);

        /*
         * Updates the provider with the new values in the map. The ListView is updated
         * automatically. The provider sets this up by setting the notification URI for
         * query Cursor objects to the incoming URI. The content resolver is thus
         * automatically notified when the Cursor for the URI changes, and the UI is
         * updated.
         * Note: This is being done on the UI thread. It will block the thread until the
         * update completes. In a sample app, going against a simple provider based on a
         * local database, the block will be momentary, but in a real app you should use
         * android.content.AsyncQueryHandler or android.os.AsyncTask.
         */
        getContentResolver().update(
                mUri,    // The URI for the record to update.
                values,  // The map of column names and new values to apply to them.
                null,    // No selection criteria are used, so no where columns are necessary.
                null     // No where columns are used, so no where arguments are necessary.
            );
    }
```
运行效果

![1](https://github.com/user-attachments/assets/3e761410-c712-4417-9ad2-953525db1f21)

![2](https://github.com/user-attachments/assets/0b1c922b-e2cb-47b2-b9a9-2475fa48236c)

![3](https://github.com/user-attachments/assets/b4dbee8c-d9d5-4184-b44e-eac621a2ba2a)

## 2.基础功能：NotePad笔记查询

在应用中添加一个搜索的按钮，点击会弹出一个搜索框，然后在搜索框中输入内容从而实现笔记查询
首先编辑list_options_menu布局，添加搜索按钮menu_search
```xml
    <item
        android:id="@+id/menu_search"
        android:icon="@android:drawable/ic_search_category_default"
        android:showAsAction="always"
        android:title="search"/>
    <item
```
新建布局文件note_search
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <SearchView
        android:id="@+id/search_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:iconifiedByDefault="false"
        android:layout_alignParentTop="true">
    </SearchView>
    <ListView
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </ListView>
</LinearLayout>
```
在NoteList的onOptionsItemSelected中添加条件
```java
 case R.id.menu_search:
                Intent intent = new Intent(this, NoteSearch.class);
                this.startActivity(intent);
                return true;
```
NoteSearch方法
```java
package com.example.android.notepad;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.example.android.notepad.Adapter.NotesAdapter;

public class NoteSearch extends ListActivity implements SearchView.OnQueryTextListener
{
    private static final String[] PROJECTION = new String[]
            {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_TIMESTAMP

    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search);
        Intent intent = getIntent();
        if (intent.getData() == null)
        {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        SearchView searchview = (SearchView)findViewById(R.id.search_view);
        searchview.setOnQueryTextListener(NoteSearch.this);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText)
    {
        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
        String[] selectionArgs = { "%"+newText+"%" };
        Cursor cursor = managedQuery(
                getIntent().getData(),
                PROJECTION,
                selection,
                selectionArgs,
                NotePad.Notes.DEFAULT_SORT_ORDER
        );
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,
                NotePad.Notes.COLUMN_NAME_TIMESTAMP };
        int[] viewIDs = { android.R.id.text1 , android.R.id.text2 };
        NotesAdapter adapter = new NotesAdapter(
                this,
                cursor,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        setListAdapter(adapter);

        return true;
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {

        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action))
        {
            setResult(RESULT_OK, new Intent().setData(uri));
        } else
        {
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}
```
运行效果

![4](https://github.com/user-attachments/assets/85ef5d95-3f76-4f0a-8d74-af3481d9d584)

![5](https://github.com/user-attachments/assets/6c8b457f-379e-4b00-936b-8927866b0f26)

![6](https://github.com/user-attachments/assets/3ae2d782-ec63-4dfd-afa4-b12aafbfff6e)

## 3.拓展功能：笔记排序（标题排序和日期排序）

在list_options_menu中添加menu_sort
```xml
    <item
        android:id="@+id/menu_sort"
        android:title="Sort"
        android:showAsAction="never">
        <menu>
            <item
                android:id="@+id/menu_sort_title"
                android:title="Sort by Title" />
            <item
                android:id="@+id/menu_sort_date"
                android:title="Sort by Date" />
        </menu>
    </item>
```
在NoteList的onOptionsItemSelected中添加条件
```java
            case R.id.menu_sort_title:
                sortNotes(NotePad.Notes.COLUMN_NAME_TITLE + " ASC");
                return true;
            case R.id.menu_sort_date:
                sortNotes(NotePad.Notes.COLUMN_NAME_TIMESTAMP + " DESC");
                return true;
```
运行效果

![7](https://github.com/user-attachments/assets/00ac8906-1b3a-46e0-a898-9732d68c4a04)

![8](https://github.com/user-attachments/assets/bc00960d-59b5-442e-9df0-ba41a264e3be)

![9](https://github.com/user-attachments/assets/93786a5b-2d6f-4895-b301-8f8c0910a9fc)

![10](https://github.com/user-attachments/assets/1d10b078-b107-47ae-b166-776f9395b31c)

## 4.拓展功能：笔记背景更换

在editor_options_menu添加menu_change_background，添加dialog_color_picker布局
```xml
    <item
        android:id="@+id/menu_change_background"
        android:title="Select Background Color"
        android:showAsAction="never"/>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<GridLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/color_grid"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@android:color/black"
    android:columnCount="6"
    android:padding="16dp"
    android:translationX="0dp"
    android:translationY="0dp">

    <View
        android:layout_width="40dp"
        android:layout_height="50dp"
        android:layout_row="0"
        android:layout_column="0"
        android:layout_margin="8dp"
        android:background="#FFFFFF"
        android:clickable="true"
        android:tag="#FFFFFF" />

    <View
        android:layout_width="40dp"
        android:layout_height="50dp"
        android:layout_margin="8dp"
        android:background="#FFFF99"
        android:clickable="true"
        android:tag="#FFFF99" />

    <View
        android:layout_width="40dp"
        android:layout_height="50dp"
        android:layout_margin="8dp"
        android:background="#FFC0CB"
        android:clickable="true"
        android:tag="#FFC0CB" />

    <View
        android:layout_width="40dp"
        android:layout_height="50dp"
        android:layout_margin="8dp"
        android:background="#ADD8E6"
        android:clickable="true"
        android:tag="#ADD8E6" />

    <View
        android:layout_width="40dp"
        android:layout_height="50dp"
        android:layout_margin="8dp"
        android:background="#FFF0F5"
        android:clickable="true"
        android:tag="#FFF0F5" />

    <View
        android:layout_width="40dp"
        android:layout_height="50dp"
        android:layout_margin="8dp"
        android:background="#BDFCC9"
        android:clickable="true"
        android:tag="#BDFCC9" />

</GridLayout>
```
在NoteEditor的onOptionsItemSelected添加条件
```java
           case R.id.menu_change_background:
                showBackgroundColorPicker();
                break;
```
showBackgroundColorPicker方法，用于更换笔记背景
```java
    private void showBackgroundColorPicker() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Background Color");
        View colorPickerView = getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        builder.setView(colorPickerView);

        AlertDialog dialog = builder.create();

        ColorClickListener clickListener = new ColorClickListener(dialog);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ViewGroup gridLayout = colorPickerView.findViewById(R.id.color_grid);
        for (int i = 0; i < gridLayout.getChildCount(); i++)
        {
            View colorView = gridLayout.getChildAt(i);
            colorView.setOnClickListener(clickListener);
        }


        dialog.show();
    }
```
运行效果

![11](https://github.com/user-attachments/assets/8f612cdc-22d9-41fc-b25c-d45ec8d3c180)

![12](https://github.com/user-attachments/assets/9199a46b-a133-4f53-acb8-0670e5d30cbf)

![13](https://github.com/user-attachments/assets/71c15df8-8b65-4391-9bb4-a65ee8dbca17)
