import io.jenetics.*;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.DoubleRange;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;
import static java.lang.Math.PI;
import static java.lang.Math.cos;

@SuppressWarnings("ALL")
public class RastriginExample {

    /**
     * Rastrigin function, see example in http://jenetics.io/manual/manual-4.3.0.pdf
     */
    private static double fitness(final double[] x) {
        int A = 10;
        int N = x.length;

        double value = A * N;
        for (int i = 0; i < N; i++) {
            value += x[i] * x[i] - A * cos(2.0 * PI * x[i]);
        }

        return value;
    }

    public static void main(final String[] args) {

        int vectorLength = 2;

        // Create a new builder with the given fitness function and chromosome
        final Engine<DoubleGene, Double> engine = Engine
                .builder(
                        JeneticsExample::fitness,
                        Codecs.ofVector(DoubleRange.of(-10.0, 10.0), vectorLength))
                .populationSize(10)
                .optimize(Optimize.MINIMUM)
                .alterers(
                        new Mutator<>(0.03),
                        new SinglePointCrossover<>(0.6))
                // Build an evolution engine with the
                // defined parameters.
                .offspringSelector(new RouletteWheelSelector<>())
                .build();
        
        // Create evolution statistics consumer.
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        
        final Phenotype<DoubleGene, Double> best = engine.stream()
                // Truncate the evolution stream after 7 "steady" generations.
                .limit(bySteadyFitness(7))
                // The evolution will stop after maximal 100 generations.
                .limit(100) 
                // Update the evaluation statistics after each generation

                .peek(statistics)
                // Collect (reduce) the evolution stream to its best phenotype.
                .collect(toBestPhenotype());
        
        System.out.println(statistics);
        System.out.println(best);
    }
}
