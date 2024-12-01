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
