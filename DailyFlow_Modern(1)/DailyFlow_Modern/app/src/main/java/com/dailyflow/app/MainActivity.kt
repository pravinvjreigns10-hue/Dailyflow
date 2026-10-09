package com.dailyflow.app

import android.app.*
import android.os.Bundle
import android.content.*
import android.graphics.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

private data class Task(var title:String,var category:String,var done:Boolean=false)

class MainActivity : Activity() {
    private lateinit var view: FlowView
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); view=FlowView(this); setContentView(view) }
    private fun addTask(){
        val input=EditText(this).apply{ hint="What do you want to finish?"; setSingleLine(true); setPadding(28,18,28,18) }
        val dialog=AlertDialog.Builder(this).setTitle("Add to your Flow").setView(input).setNegativeButton("Cancel",null).setPositiveButton("Add",null).create()
        dialog.setOnShowListener{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                val s=input.text.toString().trim()
                if(s.isEmpty()){ input.error="Enter a task"; return@setOnClickListener }
                view.tasks.add(Task(s,"Personal")); view.save(); view.invalidate(); dialog.dismiss()
            }
            input.requestFocus(); dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        dialog.show()
    }

    inner class FlowView(c:Context):View(c){
        val tasks=mutableListOf<Task>(); private val p=Paint(3); private val t=Paint(3); private val bg=Color.rgb(244,248,253); private val navy=Color.rgb(18,43,91); private val blue=Color.rgb(45,96,235); private val teal=Color.rgb(22,190,166); private val green=Color.rgb(45,194,117)
        private var tab=0; private var scroll=0f; private var downY=0f
        private val pref=getSharedPreferences("dailyflow",0)
        init{ setLayerType(View.LAYER_TYPE_SOFTWARE,null); load(); if(tasks.isEmpty()){tasks.add(Task("Complete assignment","Study",true));tasks.add(Task("Prepare presentation","Work"));tasks.add(Task("Read 10 pages","Personal"));tasks.add(Task("Plan tomorrow","Other"));save()} }
        fun save(){val a=JSONArray();tasks.forEach{x->a.put(JSONObject().apply{put("title",x.title);put("category",x.category);put("done",x.done)})};pref.edit().putString("tasks",a.toString()).apply()}
        private fun load(){pref.getString("tasks",null)?.let{try{val a=JSONArray(it);for(i in 0 until a.length()){val o=a.getJSONObject(i);tasks.add(Task(o.optString("title"),o.optString("category","Personal"),o.optBoolean("done")))}}catch(_:Exception){}}}
        override fun onDraw(c:Canvas){c.drawColor(bg);if(tab==0){c.save();c.translate(0f,-scroll);header(c);orbit(c);categories(c);next(c);taskList(c);week(c);c.restore()}else simple(c);bottom(c)}
        private fun header(c:Canvas){val w=width.toFloat();p.shader=LinearGradient(0f,0f,w,235f,Color.rgb(226,242,255),Color.rgb(255,248,226),Shader.TileMode.CLAMP);c.drawRect(0f,0f,w,235f,p);p.shader=null;p.color=Color.rgb(255,215,110);c.drawCircle(w*.78f,88f,30f,p);val q=Path();q.moveTo(0f,195f);q.lineTo(w*.24f,122f);q.lineTo(w*.43f,180f);q.lineTo(w*.64f,112f);q.lineTo(w,175f);q.lineTo(w,235f);q.lineTo(0f,235f);q.close();p.color=Color.rgb(151,202,225);c.drawPath(q,p);t.color=navy;t.textSize=30f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText("Daily",28f,55f,t);t.color=Color.rgb(42,146,225);c.drawText("Flow",118f,55f,t);t.color=navy;t.textSize=23f;c.drawText("Good morning!",28f,95f,t);t.color=Color.rgb(83,108,139);t.textSize=14f;t.typeface=Typeface.DEFAULT;c.drawText("Small steps create big results",28f,120f,t);t.color=navy;t.textSize=13f;c.drawText("TODAY",w-90f,48f,t);t.textSize=14f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText(SimpleDateFormat("EEE, d MMM",Locale.getDefault()).format(Date()),w-115f,70f,t)}
        private fun orbit(c:Canvas){val w=width.toFloat();val cx=w/2f;val cy=390f;val done=tasks.count{it.done};val pct=if(tasks.isEmpty())0 else done*100/tasks.size;p.style=Paint.Style.STROKE;p.strokeWidth=18f;p.strokeCap=Paint.Cap.ROUND;p.color=Color.rgb(224,235,246);c.drawCircle(cx,cy,78f,p);p.color=teal;c.drawArc(cx-78,cy-78,cx+78,cy+78,-90f,pct*3.6f,false,p);p.strokeCap=Paint.Cap.BUTT;p.style=Paint.Style.FILL;p.color=Color.argb(90,70,170,230);p.style=Paint.Style.STROKE;p.strokeWidth=1.5f;c.drawCircle(cx,cy,136f,p);p.style=Paint.Style.FILL;t.textAlign=Paint.Align.CENTER;t.color=navy;t.typeface=Typeface.DEFAULT_BOLD;t.textSize=15f;c.drawText("Today's Flow",cx,cy-18,t);t.textSize=38f;c.drawText("$pct%",cx,cy+24,t);t.textSize=13f;t.typeface=Typeface.DEFAULT;c.drawText("$done of ${tasks.size} tasks",cx,cy+47,t);val v=tasks.take(5);if(v.isNotEmpty())v.forEachIndexed{i,x->val a=Math.toRadians(-90.0+i*(360.0/v.size));val xx=cx+cos(a).toFloat()*136;val yy=cy+sin(a).toFloat()*136;p.color=if(x.done)green else blue;c.drawCircle(xx,yy,24f,p);t.color=Color.WHITE;t.textSize=16f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText(if(x.done)"✓" else "•",xx,yy+6,t);t.textAlign=Paint.Align.LEFT;t.color=navy;t.textSize=12f;c.drawText(x.category,xx+31,yy,t);t.color=Color.rgb(91,111,138);t.textSize=10f;t.typeface=Typeface.DEFAULT;c.drawText(if(x.done)"Done" else "Pending",xx+31,yy+15,t)};t.textAlign=Paint.Align.LEFT}
        private fun card(c:Canvas,l:Float,top:Float,r:Float,b:Float){p.color=Color.WHITE;p.setShadowLayer(12f,0f,4f,Color.argb(30,30,70,120));c.drawRoundRect(l,top,r,b,24f,24f,p);p.clearShadowLayer()}
        private fun categories(c:Canvas){val top=555f;card(c,18f,top,width-18f,top+100f);val ns=arrayOf("Study","Work","Health","Personal","Other");for(i in ns.indices){val x=55f+i*((width-110f)/4f);p.color=when(i){0->Color.rgb(45,195,140);1->blue;2->Color.rgb(145,91,224);3->Color.rgb(243,151,54);else->Color.rgb(231,92,139)};c.drawCircle(x,top+30,18f,p);val n=tasks.count{it.category==ns[i]};val d=tasks.count{it.category==ns[i]&&it.done};t.textAlign=Paint.Align.CENTER;t.color=navy;t.textSize=10f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText(ns[i],x,top+63,t);t.color=Color.GRAY;t.textSize=10f;t.typeface=Typeface.DEFAULT;c.drawText("$d/$n",x,top+80,t)};t.textAlign=Paint.Align.LEFT}
        private fun next(c:Canvas){val top=675f;card(c,18f,top,width-18f,top+105f);t.color=navy;t.textSize=17f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText("⚡  Next Up",34f,top+29,t);val n=tasks.firstOrNull{!it.done};t.textSize=15f;c.drawText(n?.title?:"Everything completed!",38f,top+65,t);t.color=Color.rgb(91,111,138);t.textSize=12f;t.typeface=Typeface.DEFAULT;c.drawText(n?.category?:"Great work",38f,top+87,t)}
        private fun taskList(c:Canvas){val top=800f;val h=70f+tasks.size*68f;card(c,18f,top,width-18f,top+h);t.color=navy;t.textSize=18f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText("Today's Tasks",34f,top+32,t);tasks.forEachIndexed{i,x->val y=top+55+i*68;p.color=if(x.done)green else Color.WHITE;c.drawRoundRect(35f,y,59f,y+24,7f,7f,p);p.style=Paint.Style.STROKE;p.strokeWidth=2f;p.color=if(x.done)green else Color.rgb(160,178,198);c.drawRoundRect(35f,y,59f,y+24,7f,7f,p);p.style=Paint.Style.FILL;if(x.done){t.color=Color.WHITE;t.textSize=16f;t.textAlign=Paint.Align.CENTER;c.drawText("✓",47f,y+18,t)};t.textAlign=Paint.Align.LEFT;t.color=if(x.done)Color.GRAY else navy;t.textSize=14f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText(x.title.take(34),75f,y+17,t);t.color=Color.rgb(100,120,145);t.textSize=11f;t.typeface=Typeface.DEFAULT;c.drawText(x.category,75f,y+38,t);t.color=Color.rgb(120,140,160);t.textSize=18f;c.drawText("⋮",width-42f,y+20,t)}}
        private fun week(c:Canvas){val top=850f+tasks.size*68f;card(c,18f,top,width-18f,top+105f);t.color=navy;t.textSize=17f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText("Weekly Progress",34f,top+30,t);t.color=Color.rgb(91,111,138);t.textSize=11f;t.typeface=Typeface.DEFAULT;c.drawText("Your Flow at a glance",34f,top+49,t);val ds=arrayOf("M","T","W","T","F","S","S");for(i in ds.indices){val x=55f+i*((width-110f)/6f);p.color=if(i==3)blue else Color.rgb(224,239,236);c.drawCircle(x,top+77,17f,p);t.textAlign=Paint.Align.CENTER;t.color=if(i==3)Color.WHITE else Color.rgb(70,105,130);t.textSize=11f;c.drawText(ds[i],x,top+81,t)};t.textAlign=Paint.Align.LEFT}
        private fun simple(c:Canvas){t.color=navy;t.textSize=28f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText(when(tab){1->"Tasks";2->"Calendar";3->"Insights";else->"Settings"},24f,70f,t);t.color=Color.rgb(91,111,138);t.textSize=15f;t.typeface=Typeface.DEFAULT;c.drawText(when(tab){1->"Your tasks are available offline.";2->"Plan your day without an internet connection.";3->"Complete tasks to grow your Flow.";else->"DailyFlow keeps your data on this device."},24f,105f,t);if(tab==1){var y=155f;tasks.forEach{x->card(c,18f,y,width-18f,y+70);t.color=navy;t.textSize=15f;c.drawText(x.title,35f,y+30,t);t.color=Color.GRAY;t.textSize=11f;c.drawText("${x.category} • ${if(x.done)"Completed" else "Pending"}",35f,y+51,t);y+=82}}}
        private fun bottom(c:Canvas){val top=height-92f;p.color=navy;c.drawRect(0f,top,width.toFloat(),height.toFloat(),p);val labels=arrayOf("Home","Tasks","+","Insights","Settings");for(i in labels.indices){val x=width*(.1f+i*.2f);t.textAlign=Paint.Align.CENTER;t.color=Color.WHITE;t.textSize=if(i==2)28f else 13f;t.typeface=Typeface.DEFAULT_BOLD;c.drawText(labels[i],x,top+40,t)};t.textAlign=Paint.Align.LEFT}
        override fun onTouchEvent(e:MotionEvent):Boolean{when(e.action){MotionEvent.ACTION_DOWN->{downY=e.y;return true};MotionEvent.ACTION_UP->{val dy=e.y-downY;if(tab==0&&kotlin.math.abs(dy)>30){scroll=(scroll-dy).coerceIn(0f,700f);invalidate();return true};val bottom=height-92f;if(e.y>=bottom){val x=e.x;tab=when{ x<width*.2->0;x<width*.4->1;x<width*.6->2;x<width*.8->3;else->4};if(tab==0&&x>=width*.4&&x<width*.6)addTask();invalidate();return true};if(tab==0){val y=e.y+scroll;val top=800f;val idx=((y-(top+55))/68).toInt();if(idx in tasks.indices&&y>=top+45){if(e.x>width-85){tasks.removeAt(idx)}else{tasks[idx].done=!tasks[idx].done};save();invalidate();return true}}}};return true}
    }
}
