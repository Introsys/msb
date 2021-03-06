package MSB2ADAPTER;

public interface StringMessageDataReaderOperations extends
        DDS.DataReaderOperations
{

  int read(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          int sample_states,
          int view_states,
          int instance_states);

  int take(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          int sample_states,
          int view_states,
          int instance_states);

  int read_w_condition(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          DDS.ReadCondition a_condition);

  int take_w_condition(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          DDS.ReadCondition a_condition);

  int read_next_sample(
          MSB2ADAPTER.StringMessageHolder received_data,
          DDS.SampleInfoHolder sample_info);

  int take_next_sample(
          MSB2ADAPTER.StringMessageHolder received_data,
          DDS.SampleInfoHolder sample_info);

  int read_instance(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          long a_handle,
          int sample_states,
          int view_states,
          int instance_states);

  int take_instance(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          long a_handle,
          int sample_states,
          int view_states,
          int instance_states);

  int read_next_instance(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          long a_handle,
          int sample_states,
          int view_states,
          int instance_states);

  int take_next_instance(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          long a_handle,
          int sample_states,
          int view_states,
          int instance_states);

  int read_next_instance_w_condition(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          long a_handle,
          DDS.ReadCondition a_condition);

  int take_next_instance_w_condition(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq,
          int max_samples,
          long a_handle,
          DDS.ReadCondition a_condition);

  int return_loan(
          MSB2ADAPTER.StringMessageSeqHolder received_data,
          DDS.SampleInfoSeqHolder info_seq);

  int get_key_value(
          MSB2ADAPTER.StringMessageHolder key_holder,
          long handle);

  long lookup_instance(
          MSB2ADAPTER.StringMessage instance);

}
