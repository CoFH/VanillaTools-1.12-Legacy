package cofh.vanillatools;

import cofh.core.item.tool.*;
import cofh.core.render.IModelRegister;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class Equipment {

	public static final Equipment INSTANCE = new Equipment();

	private Equipment() {

	}

	public static void preInit() {

		for (ToolSetVanilla e : ToolSetVanilla.values()) {
			e.preInit();
			VanillaTools.proxy.addIModelRegister(e);
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (ToolSetVanilla e : ToolSetVanilla.values()) {
			e.initialize();
		}
	}

	/* VANILLA */
	public enum ToolSetVanilla implements IModelRegister {

		// @formatter:off
    	WOOD("wood", ToolMaterial.WOOD, "plankWood") {

    		@Override
    		protected void create() {

    			itemBow = Items.BOW;
    			itemFishingRod = Items.FISHING_ROD;
				itemShears = new ItemShearsCore(material);
    			itemSickle = new ItemSickleCore(material);
    			itemHammer = new ItemHammerCore(material);
    			itemShield = Items.SHIELD;
    		}
    	},
    	STONE("stone", ToolMaterial.STONE, "cobblestone"),
    	IRON("iron", ToolMaterial.IRON, "ingotIron") {

    		@Override
    		protected void create() {

				itemBow = new ItemBowCore(material);
    			itemFishingRod = new ItemFishingRodCore(material);
				itemShears = Items.SHEARS;
    			itemSickle = new ItemSickleCore(material);
				itemHammer = new ItemHammerCore(material);
				itemShield = new ItemShieldCore(material);

    		}
    	},
    	DIAMOND("diamond", ToolMaterial.DIAMOND, "gemDiamond"),
    	GOLD("gold", ToolMaterial.GOLD, "ingotGold");
		// @formatter:on

		public final String name;
		public final String ingot;
		public final ToolMaterial material;

		/* BOW */
		private float arrowSpeed = 0.0F;
		private float arrowDamage = 0.0F;
		private float zoomMultiplier = 0.15F;

		/* FISHING ROD */
		private int luckModifier = 0;
		private int speedModifier = 0;

		/* TOOLS */
		public ItemBow itemBow;
		public ItemFishingRod itemFishingRod;
		public ItemShears itemShears;
		public ItemSickleCore itemSickle;
		public ItemHammerCore itemHammer;
		public Item itemShield;

		public ItemStack toolBow;
		public ItemStack toolFishingRod;
		public ItemStack toolShears;
		public ItemStack toolSickle;
		public ItemStack toolHammer;
		public ItemStack toolShield;

		public boolean[] enable = new boolean[6];

		ToolSetVanilla(String name, ToolMaterial materialIn, String ingot) {

			this.name = name.toLowerCase(Locale.US);
			this.ingot = ingot;
			this.material = materialIn;

			/* BOW */
			arrowDamage = material.getAttackDamage() / 4;
			arrowSpeed = material.getEfficiency() / 20;
			zoomMultiplier = MathHelper.clamp(material.getEfficiency() / 30, zoomMultiplier, zoomMultiplier * 2);

			/* FISHING ROD */
			luckModifier = material.getHarvestLevel() / 2;
			speedModifier = (int) material.getEfficiency() / 3;
		}

		protected void create() {

			itemBow = new ItemBowCore(material);
			itemFishingRod = new ItemFishingRodCore(material);
			itemShears = new ItemShearsCore(material);
			itemSickle = new ItemSickleCore(material);
			itemHammer = new ItemHammerCore(material);
			itemShield = new ItemShieldCore(material);
		}

		protected boolean enableDefault(ToolSetVanilla type) {

			return type != WOOD && type != STONE;
		}

		protected void preInit() {

			final String TOOL = "vanillaplus.tool." + name;

			String category = "Equipment.Tool." + StringHelper.titleCase(name);

			if (this != WOOD) {
				enable[0] = VanillaTools.CONFIG.getConfiguration().get(category, "Bow", enableDefault(this)).getBoolean(enableDefault(this));
				enable[1] = VanillaTools.CONFIG.getConfiguration().get(category, "FishingRod", enableDefault(this)).getBoolean(enableDefault(this));
			}
			if (this != IRON) {
				enable[2] = VanillaTools.CONFIG.getConfiguration().get(category, "Shears", enableDefault(this)).getBoolean(enableDefault(this));
			}
			enable[3] = VanillaTools.CONFIG.getConfiguration().get(category, "Sickle", enableDefault(this)).getBoolean(enableDefault(this));
			enable[4] = VanillaTools.CONFIG.getConfiguration().get(category, "Hammer", enableDefault(this)).getBoolean(enableDefault(this));

			if (this != WOOD) {
				enable[5] = VanillaTools.CONFIG.getConfiguration().get(category, "Shield", enableDefault(this)).getBoolean(enableDefault(this));
			}
			create();

			/* BOW */
			if (itemBow instanceof ItemBowCore) {
				ItemBowCore itemBow = (ItemBowCore) this.itemBow;
				itemBow.setRepairIngot(ingot).setUnlocalizedName(TOOL + "Bow").setCreativeTab(CreativeTabs.COMBAT);
				itemBow.setArrowDamage(arrowDamage).setArrowSpeed(arrowSpeed).setZoomMultiplier(zoomMultiplier);
				itemBow.setShowInCreative(enable[0]);
				itemBow.setRegistryName("tool.bow_" + name);
				ForgeRegistries.ITEMS.register(itemBow);
			}

			/* FISHING ROD */
			if (itemFishingRod instanceof ItemFishingRodCore) {
				ItemFishingRodCore itemFishingRod = (ItemFishingRodCore) this.itemFishingRod;
				itemFishingRod.setRepairIngot(ingot).setUnlocalizedName(TOOL + "FishingRod").setCreativeTab(CreativeTabs.TOOLS);
				itemFishingRod.setLuckModifier(luckModifier).setSpeedModifier(speedModifier);
				itemFishingRod.setShowInCreative(enable[1]);
				itemFishingRod.setRegistryName("tool.fishing_rod_" + name);
				ForgeRegistries.ITEMS.register(itemFishingRod);
			}

			/* SHEARS */
			if (itemShears instanceof ItemShearsCore) {
				ItemShearsCore itemShears = (ItemShearsCore) this.itemShears;
				itemShears.setRepairIngot(ingot).setUnlocalizedName(TOOL + "Shears").setCreativeTab(CreativeTabs.TOOLS);
				itemShears.setShowInCreative(enable[2]);
				itemShears.setRegistryName("tool.shears_" + name);
				ForgeRegistries.ITEMS.register(itemShears);
			}

			/* SICKLE */
			itemSickle.setRepairIngot(ingot).setUnlocalizedName(TOOL + "Sickle").setCreativeTab(CreativeTabs.TOOLS);
			itemSickle.setShowInCreative(enable[3]);
			itemSickle.setRegistryName("tool.sickle_" + name);
			ForgeRegistries.ITEMS.register(itemSickle);

			/* HAMMER */
			itemHammer.setRepairIngot(ingot).setUnlocalizedName(TOOL + "Hammer").setCreativeTab(CreativeTabs.TOOLS);
			itemHammer.setShowInCreative(enable[4]);
			itemHammer.setRegistryName("tool.hammer_" + name);
			ForgeRegistries.ITEMS.register(itemHammer);

			/* SHIELD */
			if (itemShield instanceof ItemShieldCore) {
				((ItemShieldCore) itemShield).setRepairIngot(ingot).setUnlocalizedName(TOOL + "Shield").setCreativeTab(CreativeTabs.COMBAT);
				((ItemShieldCore) itemShield).setShowInCreative(enable[5]);
				itemShield.setRegistryName("tool.shield_" + name);
				ForgeRegistries.ITEMS.register(itemShield);
			}

			toolBow = new ItemStack(itemBow);
			toolFishingRod = new ItemStack(itemFishingRod);
			toolShears = new ItemStack(itemShears);
			toolSickle = new ItemStack(itemSickle);
			toolHammer = new ItemStack(itemHammer);
			toolShield = new ItemStack(itemShield);
		}

		protected void initialize() {

			if (enable[0]) {
				addShapedRecipe(toolBow, " I#", "S #", " I#", 'I', ingot, 'S', "stickWood", '#', Items.STRING);
			}
			if (enable[1]) {
				addShapedRecipe(toolFishingRod, "  I", " I#", "S #", 'I', ingot, 'S', "stickWood", '#', Items.STRING);
			}
			if (enable[2]) {
				addShapedRecipe(toolShears, " I", "I ", 'I', ingot);
			}
			if (enable[3]) {
				addShapedRecipe(toolSickle, " I ", "  I", "SI ", 'I', ingot, 'S', "stickWood");
			}
			if (enable[4]) {
				addShapedRecipe(toolHammer, "III", "ISI", " S ", 'I', ingot, 'S', "stickWood");
			}
			if (enable[5]) {
				addShapedRecipe(toolShield, "III", "ISI", " I ", 'I', ingot, 'S', Items.SHIELD);
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {

			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(VanillaTools.MOD_ID + ":tool", "type=" + stackName));
		}

		@SideOnly (Side.CLIENT)
		public void registerModelOverride(Item item, String stackName) {

			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(VanillaTools.MOD_ID + ":tool/" + stackName, "inventory"));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {

			if (itemBow instanceof ItemBowCore) {
				registerModelOverride(itemBow, "bow_" + name);
			}
			if (itemFishingRod instanceof ItemFishingRodCore) {
				registerModelOverride(itemFishingRod, "fishing_rod_" + name);
			}
			if (itemShears instanceof ItemShearsCore) {
				registerModel(itemShears, "shears_" + name);
			}
			registerModel(itemSickle, "sickle_" + name);
			registerModel(itemHammer, "hammer_" + name);

			if (itemShield instanceof ItemShieldCore) {
				registerModelOverride(itemShield, "shield_" + name);
			}
		}
	}

}
