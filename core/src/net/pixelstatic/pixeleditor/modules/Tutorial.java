package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.gdxutils.graphics.ShapeUtils;
import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses;
import net.pixelstatic.pixeleditor.tools.TutorialStage;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Tutorial extends Module<PixelEditor>{
	private boolean active = true;
	private TutorialStage stage = TutorialStage.tooloptions;//TutorialStage.values()[0];
	private TutorialStage laststage = null;
	private float shadespeed = 0.05f;
	{
		stage.trans = 0;
	}

	@Override
	public void update(){
		
		ShapeUtils.thickness = 4;
		if(active){
			for(Rectangle rect : TutorialStage.cliprects)
				rect.set(0, 0, 0, 0);

			Core.i.stage.getBatch().begin();

			if(stage.trans < 1f){
				if(laststage != null){
					laststage.trans -= shadespeed;
					if(laststage.trans < 0) laststage.trans = 0f;
					laststage.draw(Core.i.stage.getBatch());
				}
				stage.trans += shadespeed;
				if(stage.trans > 1f) stage.trans = 1f;
				Gdx.graphics.requestRendering();

			}

			stage.draw(Core.i.stage.getBatch());

			if(stage.next){
				stage.end();
				laststage = stage;
				stage = TutorialStage.values()[stage.ordinal() + 1];
				stage.trans = 0f;
			}

			Core.i.stage.getBatch().end();
		}else{
			if(stage.trans > 0){
				Gdx.graphics.requestRendering();
				Core.i.stage.getBatch().begin();
				stage.draw(Core.i.stage.getBatch());
				Core.i.stage.getBatch().end();
				stage.trans -= shadespeed;
			}
		}
	}

	public void end(){
		active = false;
	}

	private void reset(){
		for(TutorialStage stage : TutorialStage.values()){
			stage.next = false;
			stage.trans = 1f;
		}
		stage = TutorialStage.values()[0];
		laststage = null;
		active = false;
	}

	public void begin(){
		reset();
		active = true;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button){
		if(active){
			stage.tap(x, Gdx.graphics.getHeight() - y);
			if(x > Gdx.graphics.getWidth() - 50*Core.s && (Gdx.graphics.getHeight() - y) < 30 * Core.s){
				active = false;
				new DialogClasses.ConfirmDialog("Confirm", "Are you sure you want to\nexit the tutorial?"){
					boolean confirming;
					
					public void result(){
						confirming = true;
						close();
					}
					
					public void cancel(){
						close();
					}
					
					public void close(){
						super.close();
						if(confirming){
							end();
							if(Core.i.projectmenu.getStage() != null){
								if(!Core.i.colorMenuCollapsed()) Core.i.collapseColorMenu();
								if(!Core.i.toolMenuCollapsed()) Core.i.collapseToolMenu();
								Core.i.projectmenu.hide();
							}
						}else{
							active = true;
						}
					}
					
					public void hide(){}
				}.show(Core.i.stage);
				
				
			}
		}
		return inRect(x, y);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return inRect(screenX, screenY);
	}

	public boolean inRect(int screenX, int screenY){
		if( !active) return false;
		for(Rectangle rect : TutorialStage.cliprects){
			if(rect.contains(screenX, Gdx.graphics.getHeight() - screenY)) return true;
		}
		return false;
	}
}
