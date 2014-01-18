package dna.profiler;

public class ProfilerConstants {

	public static enum ProfilerType {
		InitNodeGlobal, InitNodeLocal, InitEdgeGlobal, InitEdgeLocal, AddNodeGlobal, AddNodeLocal, AddEdgeGlobal, AddEdgeLocal, RemoveNodeGlobal, RemoveNodeLocal, RemoveEdgeGlobal, RemoveEdgeLocal, ContainsNodeGlobal, ContainsNodeLocal, ContainsEdgeGlobal, ContainsEdgeLocal, GetNodeGlobal, GetNodeLocal, GetEdgeGlobal, GetEdgeLocal, SizeNodeGlobal, SizeNodeLocal, SizeEdgeGlobal, SizeEdgeLocal, RandomNodeGlobal, RandomEdgeGlobal, IteratorNodeGlobal, IteratorNodeLocal, IteratorEdgeGlobal, IteratorEdgeLocal
	}

	public static ProfilerType[] globalEdgeListAccesses = {
			ProfilerType.InitEdgeGlobal, ProfilerType.AddEdgeGlobal,
			ProfilerType.RemoveEdgeGlobal, ProfilerType.ContainsEdgeGlobal,
			ProfilerType.GetEdgeGlobal, ProfilerType.SizeEdgeGlobal,
			ProfilerType.RandomEdgeGlobal, ProfilerType.IteratorEdgeGlobal };
	public static ProfilerType[] globalNodeListAccesses = {
			ProfilerType.InitNodeGlobal, ProfilerType.AddNodeGlobal,
			ProfilerType.RemoveNodeGlobal, ProfilerType.ContainsNodeGlobal,
			ProfilerType.GetNodeGlobal, ProfilerType.SizeNodeGlobal,
			ProfilerType.RandomNodeGlobal, ProfilerType.IteratorNodeGlobal };
	public static ProfilerType[] localEdgeListAccesses = {
			ProfilerType.InitEdgeLocal, ProfilerType.AddEdgeLocal,
			ProfilerType.RemoveEdgeLocal, ProfilerType.ContainsEdgeLocal,
			ProfilerType.GetEdgeLocal, ProfilerType.SizeEdgeLocal,
			ProfilerType.IteratorEdgeLocal };
	public static ProfilerType[] localNodeListAccesses = {
			ProfilerType.InitNodeLocal, ProfilerType.AddNodeLocal,
			ProfilerType.RemoveNodeLocal, ProfilerType.ContainsNodeLocal,
			ProfilerType.GetNodeLocal, ProfilerType.SizeNodeLocal,
			ProfilerType.IteratorNodeLocal };

}
