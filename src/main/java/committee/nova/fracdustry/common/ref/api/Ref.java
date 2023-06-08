package committee.nova.fracdustry.common.ref.api;

public interface Ref<T> {
    String getId();

    T getRef();

    @SuppressWarnings("unchecked")
    default <E extends T> E cast() {
        return (E) getRef();
    }
}
