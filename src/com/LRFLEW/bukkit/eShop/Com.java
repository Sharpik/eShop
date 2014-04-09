package com.LRFLEW.bukkit.eShop;

import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.LRFLEW.register.payment.Method;
import com.LRFLEW.register.payment.Method.MethodAccount;
import com.LRFLEW.register.payment.Methods;

public class Com implements CommandExecutor
{
        private EShop instance = EShop.getInstance();
	final HashMap<MaterialData, Pricing> costs;
	final HashMap<String, MaterialData> iLocal;
	final Prefs prefs;
	final Perms perms;
	
        
        //private EShop plugin;
        public Com(HashMap<MaterialData, Pricing> costs, HashMap<String, MaterialData> iLocal, Prefs prefs, Perms perms)
        {
		this.costs = costs;
		this.iLocal = iLocal;
		this.prefs = prefs;
		this.perms = perms;
                //this.plugin = plugin;
                // System.out.println("Debug: plnim parametry");
	}
        
        //@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
        {
            if (sender instanceof Player)
            {
                //sender.sendMessage("Debug: zkousim comannd");
                 //System.out.println("Debug: zkousim comannd");
                    if ( label.equalsIgnoreCase("shop") )
                    {
                        //sender.sendMessage("Debug: jsem v comanndu, delka " + args.length);
                        //System.out.println("Debug: jsem v comanndu, delka " + args.length);
                            if (sender instanceof Player && args.length >= 2)
                            {
                                    //sender.sendMessage("Debug: mam dost argumentů");
                                    Player player = (Player)sender;
                                    Method method = Methods.getMethod();
                                    if (method == null) return false;
                                    MethodAccount account = Methods.getMethod().getAccount(player.getName());
                                    MethodAccount eShopA = Methods.getMethod().getAccount("eShop");
                                    Pricing price = null;
                                    String[] item = args[1].split(":");
                                    if (account != null)
                                    {
                                        //sender.sendMessage("Debug: mam ucet");
                                            MaterialData md;
                                            if (iLocal.containsKey(item[0].toLowerCase()))
                                            {
                                                    md = iLocal.get(item[0].toLowerCase());
                                                    if (md.getItemType().getData() != null && item.length >= 2)
                                                    {
                                                            md = md.getItemType().getNewData((byte)-1);
                                                            price = costs.get(md);
                                                            if (!Materializer.setMD(md, item[1]))
                                                            {
                                                                    prefs.sendMessage(sender, "eMD");
                                                                    return true;
                                                            }
                                                    }
                                                    else
                                                    {
                                                            md = new MaterialData(md.getItemType(), (byte)-1);
                                                            if (!costs.containsKey(md)) md.setData((byte)0);
                                                    }
                                            }
                                            else
                                            {
                                                    Material m = null;
                                                    if (m == null) m = Material.matchMaterial(item[0]);
                                                    if (m == null)
                                                    {
                                                            prefs.sendMessage(sender, "eM");
                                                            return false;
                                                    }
                                                    if (m.getData() != null && item.length >= 2)
                                                    {
                                                            md = m.getNewData((byte)-1);
                                                            price = costs.get(md);
                                                            if (!Materializer.setMD(md, item[1]))
                                                            {
                                                                    prefs.sendMessage(sender, "eMD");
                                                                    return true;
                                                            }
                                                    }
                                                    else
                                                    {
                                                            md = new MaterialData(m, (byte)-1);
                                                            if (!costs.containsKey(md)) md.setData((byte)0);
                                                    }
                                            }
                                            if (price == null) price = costs.get(md);
                                            if (args[0].equalsIgnoreCase("sell"))
                                            {
                                                    if (perms.hasPermission(player, "eShop.sell"))
                                                    {
                                                            if (price != null && price.sell != 0)
                                                            {
                                                                try
                                                                {
                                                                    if (args.length == 2) sellItem(player, method, account, md, 1, price, eShopA);
                                                                    else
                                                                    {
                                                                            if (args[2].equalsIgnoreCase("all"))
                                                                            {
                                                                                    sellItem(player, method, account, md, 2147483647, price, eShopA);
                                                                                    return true;
                                                                            }
                                                                            else
                                                                            {
                                                                                    int amount;
                                                                                    try
                                                                                    {
                                                                                            amount = Integer.parseInt(args[2]);
                                                                                            if (amount <= 0) throw new NumberFormatException();
                                                                                    }
                                                                                    catch (NumberFormatException e)
                                                                                    {
                                                                                            prefs.sendMessage(sender, "eA");
                                                                                            return false;
                                                                                    }
                                                                                    sellItem(player, method, account, md, amount, price, eShopA);
                                                                                    return true;
                                                                            }
                                                                    }
                                                                    return true;
                                                                }
                                                                catch (InterruptedException ex)
                                                                {
                                                                    Logger.getLogger(Com.class.getName()).log(Level.SEVERE, null, ex);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                prefs.sendMessage(sender, "eNS", md);
                                                                return true;
                                                            }
                                                    }
                                                    else
                                                    {
                                                        prefs.sendMessage(sender, "pS");
                                                        return true;
                                                    }
                                            }
                                            if (args[0].equalsIgnoreCase("buy"))
                                            {
                                                    if (perms.hasPermission(player, "eShop.buy"))
                                                    {
                                                            if (price != null && price.buy != 0)
                                                                {
                                                                        if (args.length == 2) buyItem(player, method, account, md, 1, price, eShopA);
                                                                        else
                                                                        {
                                                                            int amount;
                                                                            try
                                                                            {
                                                                                    amount = Integer.parseInt(args[2]);
                                                                                    if (amount <= 0) throw new NumberFormatException();
                                                                            }
                                                                            catch (NumberFormatException e)
                                                                            {
                                                                                    prefs.sendMessage(sender, "eA");
                                                                                    return false;
                                                                            }
                                                                            buyItem(player, method, account, md, amount, price, eShopA);
                                                                            return true;
                                                                        }
                                                                        return true;
                                                                }
                                                            else
                                                            {
                                                                    prefs.sendMessage(sender, "eNB", md);
                                                                    return true;
                                                            }
                                                    }
                                                    else
                                                    {
                                                            prefs.sendMessage(sender, "pB");
                                                            return true;
                                                    }
                                            }
                                            if (args[0].equalsIgnoreCase("check"))
                                            {
                                                    if (perms.hasPermission(player, "eShop.check"))
                                                    {
                                                            if (md.getData() == -1) md.setData((byte)0);
                                                            if (price != null)
                                                            {
                                                                    if (price.buy == 0)
                                                                    {
                                                                            prefs.sendMessage(sender, "bS", md, method.format(price.sell), method.format(eShopA.balance()));
                                                                    }
                                                                    else if (price.sell == 0)
                                                                    {
                                                                            prefs.sendMessage(sender, "Bs", md, method.format(price.buy), method.format(eShopA.balance()));
                                                                    }
                                                                    else
                                                                    {
                                                                            prefs.sendMessage(sender, "BS", md, method.format(price.buy), method.format(price.sell), method.format(eShopA.balance()));
                                                                    }
                                                                    return true;
                                                            }
                                                            else
                                                            {
                                                                    prefs.sendMessage(sender, "bs", md, method.format(eShopA.balance()));
                                                                    return true;
                                                            }
                                                    }
                                                    else
                                                    {
                                                            prefs.sendMessage(sender, "pC");
                                                            return true;
                                                    }
                                            }
                                    }
                            }
                    }
                    else
                    {
                        //sender.sendMessage("Debug: nemuzu najit command");
                        return false;
                    }
                }
                else
                {
                    sender.sendMessage("You must be a player!");
                    return false;
                }
                
		return false;
	}
        
        //price modifier
	double modifi = (Math.random()*3) + 1.5;
        double monycharge = 10.0;
        int timewait = 30000;
        
	private void sellItem( Player player, Method method, MethodAccount account, MaterialData md, int amount, Pricing price, MethodAccount eShopA ) throws InterruptedException
        {
                int amount2 = 0;
                if (price.buy != price.sell)
                {
                    amount2 = (int)Math.floor(eShopA.balance()/price.sell);
                    if ( amount2 < 1 )
                    {
                            prefs.sendMessage(player, "tA2", method.format(eShopA.balance()));
                                /*if ( eShopA.balance() < monycharge )
                                {
                                    Thread.sleep(timewait);
                                    if ( eShopA.balance() < monycharge ) { eShopA.set(monycharge); }
                                }*/
                            if ( eShopA.balance() < monycharge ) { eShopA.set(monycharge); }
                            return;
                    }
                    if ( amount > amount2 )
                    {
                        amount = amount2;
                    }
                }
                if (amount == 0)
                {
                    prefs.sendMessage(player, "tN", method.format(eShopA.balance()));
                    return;
                }
                Inventory in = player.getInventory();
                int t;
                if (md.getData() == -1) t = removeItem(in, new ItemStack(md.getItemTypeId(), amount));
                else t = removeItemMD(in, new ItemStack(md.getItemType(), amount, md.getData()));
                if (t != 0) amount -= t;
                if ( amount + t == amount2 ) { prefs.sendMessage(player, "tA3", amount, md); }
                if (amount == 0)
                {
                    prefs.sendMessage(player, "tA", md);
                    return;
                }
                account.add((price.sell)*amount);
                if (price.buy != price.sell) { eShopA.subtract((price.sell)*amount); }

                prefs.sendMessage(player, "S", Integer.toString(amount) + " " + md,
                                method.format(price.sell*amount), method.format(account.balance()), method.format(eShopA.balance()));
                System.out.println( player.getDisplayName() + " has sold: " + md + " " + Integer.toString(amount) + " for: " + method.format(price.sell*amount) + ", now have: " + method.format(account.balance()) + ", eShop have: " + method.format(eShopA.balance()) );
                /*if ( eShopA.balance() < monycharge )
                {
                    Thread.sleep(timewait);
                    if ( eShopA.balance() < monycharge ) { eShopA.set(monycharge); }
                }*/
                if ( eShopA.balance() < monycharge ) { eShopA.set(monycharge); }
	}
	
	private void buyItem( Player player, Method method, MethodAccount account, MaterialData md, int amount, Pricing price, MethodAccount eShopA )
        {
		if (amount == 0)
                {
			prefs.sendMessage(player, "tN");
			return;
		}
		Inventory in = player.getInventory();
                double modiffi = 1;
		
		if (!account.hasEnough(price.buy*amount)) amount = (int) Math.floor(account.balance()/price.buy);
		if (amount == 0)
                {
			prefs.sendMessage(player, "tM", md);
			return;
		}
		if (md.getData() == -1) md.setData((byte)0);
                //int t = addItem(in, new ItemStack(md.getItemType(), amount));
		int t = addItem(in, new ItemStack(md.getItemTypeId(), amount, md.getData()));
		if (t != 0) amount -= t;
		if (amount == 0)
                {
			prefs.sendMessage(player, "tR");
			return;
		}
                if (price.buy != price.sell) { modiffi = modifi; }
		account.subtract(price.buy*amount);
                if (price.buy != price.sell) { eShopA.add((price.buy)*amount*modiffi); }
		prefs.sendMessage(player, "B", Integer.toString(amount) + " " + md,
				method.format(price.buy*amount), method.format(account.balance()), method.format(eShopA.balance()));
                System.out.println( player.getDisplayName() + " has bought: " + md + " " + Integer.toString(amount) + " for: " + method.format(price.buy*amount) + ", now have: " + method.format(account.balance()) + ", eShop have: " + method.format(eShopA.balance()) );
	}
	
	public static int removeItemMD(Inventory inventory, ItemStack item)
        {
		
		int toDelete = item.getAmount();
		while (true)
                {
			int first;
			if (item.getData() != null) first = first(inventory, item.getData());
			else first = first(inventory, new MaterialData(item.getType(), (byte)0));

			// Drat! we don't have this type in the inventory
			if (first == -1)
                        {
				return toDelete;
			}
                        else
                        {
				ItemStack itemStack = inventory.getItem(first);
				int amount = itemStack.getAmount();
				if (amount <= toDelete)
                                {
					toDelete -= amount;
					// clear the slot, all used up
					inventory.clear(first);
				}
                                else
                                {
						// split the stack and store
					itemStack.setAmount(amount - toDelete);
					inventory.setItem(first, itemStack);
					toDelete = 0;
				}
			}
				// Bail when done
			if (toDelete <= 0)
                        {
				return 0;
			}
		}
	}
	
	public static int first(Inventory in, MaterialData md)
        {
		ItemStack[] inventory = in.getContents();
		for (int i = 0; i < inventory.length; i++)
                {
			ItemStack item = inventory[i];
			if (item != null)
                        {
                            MaterialData t = item.getData();
                            if (t != null)
                            {
                                    if (t.equals(md))
                                    {
                                            return i;
                                    }
                            }
                            else
                            {
                                    if (item.getType().getMaxDurability() != -1 && item.getDurability() != 0) continue;
                                    System.out.println(item.getType().getMaxDurability() + " + " + item.getDurability());
                                    if (item.getTypeId() == md.getItemTypeId())
                                    {
                                            return i;
                                    }
                            }
			}
		}
		return -1;
	}
	
	public static int addItem(Inventory in, ItemStack is)
        {
		HashMap<Integer, ItemStack> t = in.addItem(is);
		if (t != null && t.get(0) != null) return t.get(0).getAmount();
		else return 0;
	}
	
	public static int removeItem(Inventory in, ItemStack is)
        {
		HashMap<Integer, ItemStack> t = in.removeItem(is);
		if (t != null && t.get(0) != null) return t.get(0).getAmount();
		else return 0;
	}
	
}
