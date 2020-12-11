package timicasto.quantumelectrix.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.RegistryHandler;

import java.util.Random;

public class OreGen implements IWorldGenerator {
    private final WorldGenerator azurite, bornite, chalcocite, platinum, pyrites;
    private final Logger logger = LogManager.getLogger();

    public OreGen() {
        Random random = new Random();
        azurite = new WorldGenMinable(RegistryHandler.AZURITE.getDefaultState(), random.nextInt(8) + 4);
        bornite = new WorldGenMinable(RegistryHandler.BORNITE.getDefaultState(), random.nextInt(6) + 2);
        chalcocite = new WorldGenMinable(RegistryHandler.CHALCOCITE.getDefaultState(), random.nextInt(10) + 6);
        platinum = new WorldGenMinable(RegistryHandler.PLATINUM_ORE.getDefaultState(), random.nextInt(4) + 2);
        pyrites = new WorldGenMinable(RegistryHandler.PYRITES.getDefaultState(), random.nextInt(2) + 2);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            generateOre(azurite, RegistryHandler.AZURITE, world, random, chunkX, chunkZ, 14, 7, 39);
            generateOre(bornite, RegistryHandler.BORNITE, world, random, chunkX, chunkZ, 27, 9, 43);
            generateOre(platinum, RegistryHandler.PLATINUM_ORE, world, random, chunkX, chunkZ, 5, 2, 32);
            generateOre(chalcocite, RegistryHandler.CHALCOCITE, world, random, chunkX, chunkZ, 28, 6, 50);
            generateOre(pyrites, RegistryHandler.PYRITES, world, random, chunkX, chunkZ, 8, 12, 30);
        }
    }

    private void generateOre(WorldGenerator generator, Block generateSource, World world, Random random, int chunkX, int chunkZ, int chance, int minY, int maxY) {
        if (minY > maxY || minY < 0 || maxY > 256) {
            throw new IllegalArgumentException("Ore generated out of bounds");
        }

        int deltaY = maxY - minY + 1;
        for (int i = 0; i < chance; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = minY + random.nextInt(deltaY);
            int z = chunkZ * 16 + random.nextInt(16);

            generator.generate(world, random, new BlockPos(x, y, z));
            logger.info("generated " + generateSource + " at " + new BlockPos(x, y, z));
        }
    }
}
