package committee.nova.fracdustry;

import committee.nova.fracdustry.common.handler.registry.RegistryHandler;
import net.minecraftforge.fml.common.Mod;

@Mod(FractalIndustry.MODID)
public class FractalIndustry {
    public static final String MODID = "fracdustry";

    public FractalIndustry() {
        RegistryHandler.init();
    }
}
