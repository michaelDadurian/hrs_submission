#!/usr/bin/env python3
import logging
import sys
import os
import json
import operator

log = logging.getLogger(__name__)

operators = {
    '<': operator.lt,
    '>': operator.gt,
    '=': operator.eq
}

'''
These sets keep track of currently active predicates
and predicates that have terminated (can no longer be True)
'''
active_predicates = set()
discard_predicates = set()

'''
If I were to port this to Java, each of the below classes would implement
an interface that defines a function process_predicate(element, index).

To generalize the creation of our rules, each predicate class has the same constructor.
'''
class Comparison:
    def __init__(self, json_config, start_index, start_element):
        self.rule_id = json_config['id']
        self.type = json_config['type']
        self.comparison_fn = operators[json_config['check']]
        self.threshold = json_config['value']

        self.start_index = start_index
        self.start_element = start_element

    def process_predicate(self, curr_element=None, curr_index=None):
        result = self.comparison_fn(self.start_element, self.threshold)
        # Since we are only checking the current element, discard this predicate once we compute the comparison
        discard_predicates.add(self)

        return (result, self.start_index)


class Delta:
    def __init__(self, json_config, start_index, start_element):
        self.rule_id = json_config['id']
        self.type = json_config['type']
        self.comparison_fn = operators[json_config['check']]
        self.change_value = json_config['change']
        self.range = json_config['over']

        self.start_element = start_element
        self.end_index = start_index + self.range

    def process_predicate(self, curr_element=None, curr_index=None):
        result = False
        if curr_index < self.end_index:
            result = self.comparison_fn(curr_element - self.start_element, self.change_value)

            # Discard the predicate if we have found it to be True 
            if result: 
                discard_predicates.add(self)

        else: 
            # Discard this predicate if we have gone over the allowed range
            discard_predicates.add(self)


        return (result, curr_index)


class Composition:
    def __init__(self, json_config, start_index, start_element):
        self.rule_id = json_config['id']
        self.all_predicates = json_config['all']
        self.predicates = []

        # Initialize predicate objects
        for predicate in self.all_predicates:
            pred_obj = predicate_type_map[predicate['type']](predicate, start_index, start_element)
            self.predicates.append(pred_obj)

        self.start_element = start_element
        
    def process_predicate(self, curr_element=None, curr_index=None):
        predicate_results = []
        are_all_comparisons = True

        # Process each predicate and store their corresponding results
        for predicate in self.predicates:
            are_all_comparisons &= predicate.type == 'comparison'
            pred_result = predicate.process_predicate(curr_element, curr_index)[0]

            predicate_results.append((predicate, pred_result))

        result = False
        if are_all_comparisons:
            result = predicate_results[0][1]

        for pred_result in predicate_results:
            # If all the predicates are comparisons, AND the results together
            if are_all_comparisons:
                result &= pred_result[1]

            # Otherwise, OR the results together
            else:
                result |= pred_result[1]

            # If any of the predicates terminated, discard this predicate
            if pred_result[0] in discard_predicates:
                discard_predicates.add(self)

        return (result, curr_index)
        

class Pattern:
    def __init__(self, json_config, start_index, start_element):
        self.rule_id = json_config['id']
        self.type = json_config['type']
        self.pattern_list = json_config['pattern']

        self.start_index = start_index
        self.start_element = start_element

        self.pattern_start = self.pattern_list[0]
        self.pattern_index = 0

    def process_predicate(self, curr_element=None, curr_index=None):
        if self.pattern_start == curr_element:
            self.pattern_index += 1
            if self.pattern_index == len(self.pattern_list):
                return (True, curr_index)
        
            # Get the next element to match
            self.pattern_start = self.pattern_list[self.pattern_index]
        else:
            # As soon as we find an element that is not in the pattern, discard the predicate
            discard_predicates.add(self)

        return (False, curr_index)

# Mapping of predicate types to their corresponding objects
predicate_type_map = {
    'comparison': Comparison,
    'delta': Delta,
    'pattern': Pattern,
    'composition': Composition
}
    
def is_valid_file(f):
    if not os.path.exists(f):
        return False
    
    return True


def make_predicate(predicate, index, element):
    pred_obj = predicate_type_map[predicate['type']](predicate, index, element)
    return pred_obj


def process_signals(config_data, values):
    global active_predicates
    global discard_predicates

    for index, element in enumerate(values):
        # For every input value, create a new predicate for each of the rules
        for predicate in config_data:
            pred_obj = make_predicate(predicate, index, element)
            # Consider them active
            active_predicates.add(pred_obj)

        # Process each active predicate
        for active_predicate in active_predicates:
            # Each call to process_predicate() will decide if the predicate could still be valid (update discard_predicates)
            triggered, at_index = active_predicate.process_predicate(element, index)
            if triggered:
                print('{}@{}\n'.format(active_predicate.rule_id, at_index))
        
        # Reset our discarded predicates and update our active predicates
        active_predicates = active_predicates.difference(discard_predicates)
        discard_predicates = set()
        

def main(args):
    config_file = args.Config
    input_file = args.Input

    if not is_valid_file(config_file):
        log.error('Config file not found')
        sys.exit(1)
    
    if not is_valid_file(input_file):
        log.error('Input file not found')
        sys.exit(1)

    # Parse our input files
    config_data = None
    with open(config_file, 'r') as cf:
        config_data = json.load(cf)

    values = []
    with open(input_file, 'r') as fi:
        fi.readline() # skip first line
        for line in fi:
            values.append(int(line))

    process_signals(config_data, values)


if __name__ == '__main__':
    import argparse
    
    parser = argparse.ArgumentParser(description='Signal sounder')
    parser.add_argument('Config',
                        metavar='config_path',
                        type=str,
                        help='the path to the config file')

    parser.add_argument('Input',
                        metavar='input_path',
                        type=str,
                        help='the path to the input file')

    args = parser.parse_args()

    main(args)