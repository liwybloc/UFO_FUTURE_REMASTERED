
package com.raishxn.ufo.item.custom.cell;

import appeng.api.stacks.AEKeyType;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import com.raishxn.ufo.item.ModCellItems;
import net.minecraft.world.item.Item;

/**
 * Classe base para as novas células de armazenamento customizadas.
 */
public class ModCellItem extends BasicStorageCell {

    public ModCellItem(final StorageTier tier, final int maxTypes, final AEKeyType keyType) {
        super(
                new Item.Properties().stacksTo(1),
                tier.idleDrain(),
                calculateKibiBytes(tier),
                calculateBytesPerType(tier),
                maxTypes,
                keyType
        );
    }

    private static int calculateKibiBytes(final StorageTier tier) {
        if (tier == ModCellItems.TIER_INFINITY) {
            return Integer.MAX_VALUE /  1024;
        }
        return tier.bytes() / 1024;
    }

    private static int calculateBytesPerType(final StorageTier tier) {
        if (tier == ModCellItems.TIER_INFINITY) {
            return 262144;
        }
        return 1;
    }
}
