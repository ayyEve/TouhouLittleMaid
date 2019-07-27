package com.github.tartaricacid.touhoulittlemaid.compat.hwyla;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;

/**
 * @author TartaricAcid
 * @date 2019/7/25 19:46
 **/
public class EntityMaidProvider implements IWailaEntityProvider {
    @Nonnull
    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (entity instanceof EntityMaid) {
            EntityMaid maid = (EntityMaid) entity;
            if (maid.isTamed()) {
                currenttip.add(I18n.format("hwyla.touhou_little_maid.entity_maid.task",
                        I18n.format(maid.getTask().getTranslationKey())));
            }
        }
        return currenttip;
    }
}
