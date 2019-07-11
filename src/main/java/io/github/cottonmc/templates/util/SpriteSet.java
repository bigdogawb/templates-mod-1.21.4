package io.github.cottonmc.templates.util;

import java.util.List;
import java.util.Random;

import net.minecraft.client.texture.MissingSprite;
import org.apache.commons.lang3.ObjectUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SpriteSet {
	private Object2ObjectOpenHashMap<Direction, BakedQuad> quads = new Object2ObjectOpenHashMap<>();
	private boolean isDefault = true;
	public static final Sprite DEFAULT = MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier("minecraft:block/scaffolding_top"));
	public static final Sprite FALLBACK = MissingSprite.getMissingSprite();

	public SpriteSet() {
	    clear();
	}

	/** Allow re-use of instances to avoid allocation in render loop */
	public void clear() {
	    isDefault = true;
	}
	
	/** Allow re-use of instances to avoid allocation in render loop */
	//TODO: pass in block state?
    public void prepare(BakedModel model, Random rand) {
        this.quads.clear();
        isDefault = false;
        // avoid Direction.values() in hot loop - for thread safety may generate new array instances
        //for (Direction dir : Direction.values()) {
        for(int i = 0; i < 6; i++) {
            final Direction dir = ModelHelper.faceFromIndex(i);
            List<BakedQuad> quads = model.getQuads(null, dir, rand);
            if (!quads.isEmpty()) this.quads.put(dir, quads.get(0));
        }
    }

	public Sprite getSprite(Direction dir) {
    	if (isDefault) return DEFAULT;
		BakedQuad quad = quads.get(dir);
		if (quad == null) return FALLBACK;
		return quad.getSprite();
	}

	public boolean hasColor(Direction dir) {
    	if (isDefault) return false;
    	BakedQuad quad = quads.get(dir);
    	if (quad == null) return false;
	    return quad.hasColor();
	}
}
