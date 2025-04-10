package me.matl114.matlib.utils.version;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.UUID;

public abstract class VersionedAttribute {
    private static VersionedAttribute Instance;
    public static VersionedAttribute getInstance(){
        if(Instance == null){
            init0();
        }
        return Instance;
    }

    private static void init0(){

    }
    public abstract AttributeModifier createAttributeModifier(UUID uid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot);
    public abstract String getAttributeModifierName(AttributeModifier modifier);
    public abstract boolean setAttributeModifierValue(AttributeModifier modifier, double value);
    public abstract UUID getAttributeModifierUid(AttributeModifier modifier);
    public abstract EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier);
    static class  Default extends VersionedAttribute{
        Field field;
        {
            try{
                field = AttributeModifier.class.getDeclaredField("amount");
                field.setAccessible(true);
            }catch (Throwable e){
                throw new RuntimeException( e);
            }
        }

        @Override
        public AttributeModifier createAttributeModifier(UUID uid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
            return new AttributeModifier(uid,name,amount,operation,slot);
        }

        @Override
        public String getAttributeModifierName(AttributeModifier modifier) {
            return modifier.getName();
        }

        @Override
        public boolean setAttributeModifierValue(AttributeModifier modifier, double value) {
            try{
                field.set(modifier, value);
                return true;
            }catch (Throwable e){
                return false;
            }
        }

        @Override
        public UUID getAttributeModifierUid(AttributeModifier modifier) {
            return modifier.getUniqueId();
        }

        @Override
        public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier) {
            return modifier.getSlot();
        }
    }
}
