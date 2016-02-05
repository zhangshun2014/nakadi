package de.zalando.aruha.nakadi.repository;

import de.zalando.aruha.nakadi.NakadiException;
import de.zalando.aruha.nakadi.domain.Topic;
import de.zalando.aruha.nakadi.domain.TopicPartition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class InMemoryTopicRepository implements TopicRepository {

    public static final int DEFAULT_NUMBER_OF_PARTITIONS = 3;
    private final Map<String, MockTopic> topics;

    public InMemoryTopicRepository() {
        topics = new HashMap<>();
    }

    @Override
    public List<Topic> listTopics() throws NakadiException {
        return topics.values().stream().map(mockTopic -> new Topic(mockTopic.name)).collect(toList());
    }

    @Override
    public void createTopic(final String topic) {
        createTopic(topic, DEFAULT_NUMBER_OF_PARTITIONS);
    }

    public void createTopic(final String topicId, final int partitionsNum) {
        topics.put(topicId, new MockTopic(topicId, partitionsNum));
    }

    @Override
    public void createTopic(final String topic, final int partitionsNum, final int replicaFactor,
            final long retentionMs, final long rotationMs) {
        topics.put(topic, new MockTopic(topic, partitionsNum));
    }

    @Override
    public void postEvent(final String topicId, final String partitionId, final String payload) throws NakadiException {
        getPartition(topicId, partitionId).postEvent(payload);
    }

    private MockPartition getPartition(final String topicId, final String partitionId) throws NakadiException {
        final MockTopic topic = topics.get(topicId);
        if (topic == null) {
            throw new NakadiException("No such topic");
        }

        final MockPartition partition = topic.partitions.get(partitionId);
        if (partition == null) {
            throw new NakadiException("No such partition");
        }

        return partition;
    }

    @Override
    public List<TopicPartition> listPartitions(final String topicId) throws NakadiException {
        final MockTopic mockTopic = topics.get(topicId);
        if (mockTopic == null) {
            throw new NakadiException("No such topic");
        }

        return mockTopic.partitions.values().stream().map(p -> new TopicPartition(topicId, p.id)).collect(toList());
    }

    @Override
    public boolean validateOffset(final String offsetToCheck, final String newestOffset, final String oldestOffset) {
        throw new UnsupportedOperationException("Implement this method if needed");
    }

    @Override
    public EventConsumer createEventConsumer(final String topic, final Map<String, String> cursors) {
        throw new UnsupportedOperationException("Implement this method if needed");
    }

    public LinkedList<String> getEvents(final String topicId, final String partitionId) throws NakadiException {
        return getPartition(topicId, partitionId).events;
    }

    private static class MockTopic {
        private final String name;
        private final Map<String, MockPartition> partitions;

        private MockTopic(final String name, final int numberOfPartitions) {
            this.name = name;
            partitions = new HashMap<>();
            for (int i = 1; i <= numberOfPartitions; i++) {
                final String partitionId = String.valueOf(i);
                partitions.put(partitionId, new MockPartition(partitionId));
            }
        }
    }

    private static class MockPartition {
        private final String id;
        private LinkedList<String> events = new LinkedList<>();

        private MockPartition(final String id) {
            this.id = id;
        }

        public void postEvent(final String payload) {
            events.add(payload);
        }
    }
}