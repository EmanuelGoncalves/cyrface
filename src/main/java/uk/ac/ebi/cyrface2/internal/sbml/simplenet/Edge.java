package uk.ac.ebi.cyrface2.internal.sbml.simplenet;

public interface Edge
{

	public abstract Node getDest();

	public abstract Node getSrc();

	public abstract String toString();

	public abstract String getPredictate();

}