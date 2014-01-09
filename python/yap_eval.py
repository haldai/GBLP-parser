import sys,os,signal
import timeit,time
import subprocess as sp

class Input :
    sentList = []

    def readFile(self, filename) :
        fin = open(filename, 'r')
        while True :
            line = fin.readline()
            if not line :
                break
            line = line.strip()
            if (line.find('sentence') > 0) :
                tmp_sent = []
            elif (len(line) == 0) :
                self.sentList.append(tmp_sent)
            else :
                tmp_sent.append(line)
        fin.close()

def process_fact(prob_fact) :
    # Line parser for ground program file
    prob, atom = prob_fact.split('::')
    atom = atom.rstrip('.')
    return atom, float(prob)

def main(argv) :
    if len(argv) < 2 :
        print 'usage: yap_eval.py [input_path] [model_dir] ([label_path] [sentence_path])'
        exit(0)
        
    input_path = argv[0]
    model_dir = os.path.abspath(os.curdir) + '/' + argv[1] + '/'
    if len(argv) >= 3 :
        label_path = os.path.abspath(os.curdir) + '/' + argv[2]
        flabel = open(label_path, 'r')

    if len(argv) >= 4 :
        sent_path =  os.path.abspath(os.curdir) + '/' + argv[3]
        fsent_str = open(sent_path, 'r')
    
    tmp_dir = 'tmp/'
    tmp_dir = os.path.abspath(os.curdir) + '/' + tmp_dir
    query_file = tmp_dir + 'tmp_query.pl'

    yap_cmd = 'yap -L ../prolog/ground_hack.pl -- [model] [query] [input] [ground_prog] [evidence] [ground_query]'

    
    data = Input()
    data.readFile(input_path)

    tmp_file = tmp_dir + 'tmp_sent.pl'
    tmp_ground_prog = tmp_dir + 'tmp_ground_prog.pl'
    tmp_evidence = tmp_dir + 'tmp_evidence.pl'
    tmp_ground_query = tmp_dir + 'tmp_ground_query.pl'
    
    tmp_yap_cmd = yap_cmd.replace('[query]', query_file)
    tmp_yap_cmd = tmp_yap_cmd.replace('[ground_prog]', tmp_ground_prog)
    tmp_yap_cmd = tmp_yap_cmd.replace('[evidence]', tmp_evidence)
    tmp_yap_cmd = tmp_yap_cmd.replace('[ground_query]', tmp_ground_query)

    tmp_yap_cmd = tmp_yap_cmd.replace('[input]', tmp_file)
    tmp_yap_cmd = tmp_yap_cmd.replace('[query]', query_file)

    # read file
    total_sent = 0
    total_label = 0
    right_label = 0
    wrong_label = 0
    missed_label = 0
    uncovered_sent = 0
    right_sent = 0

    fresult = open("out/result", 'w')
    fresult_data_one = open('out/result.data.one', 'w')
    fresult_data_multi = open('out/result.data.multi', 'w')
    
    tTotal = 0.0
    
    for sent in data.sentList :
        #tStart = time.time()
        # sentence number
        total_sent = total_sent + 1
        if len(argv) >= 3 :
            labels = flabel.readline().strip().split('\t')
        else :
            labels = []

        # result string
        if len(argv) >= 4 :
            sent_str = fsent_str.readline().strip()
        else :
            sent_str = ""
        re_str = sent_str + "\t"
        if len(argv) >= 3 :
            for l in labels :
                re_str = re_str + l + ";"
            re_str = re_str[:-1] + "\t"
            if len(labels) < 0 :
                continue
        # print to tmp_file
        fsent = open(tmp_file, 'w')
        for line in sent :
            fsent.write('%s\n' % line)
        fsent.flush()

        result = {}
        # for each model.pl to evaluate
        models = os.listdir(model_dir)
        for model_name in models :
            if (model_name[-3:] == '.pl') and (model_name[0:5] == 'rules'):
                model_path = model_dir + model_name
            else :
                continue
            model_file = open(model_path, 'r')
            first_line = model_file.readline().strip()

            # weight of current model
            cur_weight = float(first_line.split('=')[1])
            
            cmd = tmp_yap_cmd.replace('[model]', model_path)

            # call yap
            tStart = time.time()
            sp.call(cmd, shell = True)
            tEnd = time.time()
            tTotal = tEnd - tStart + tTotal
            

            # Read probabilities and ground program from Yap's output
            probs = {}
            with open(tmp_ground_prog, 'r') as in_file :
                for line in in_file :
                    if '::' in line :
                        prob_fact = line.strip()
                        atom, prob = process_fact(prob_fact)
                        probs[atom] = prob

            # TODO modify probability
            for p in probs :
                c_weight = float(probs[p])
                c_weight = float(c_weight - 0.5)*2*cur_weight
                if p in result :
                    t_weight = result[p]
                    result[p] = t_weight + c_weight
                else :
                    result[p] = c_weight
        #tEnd = time.time()
        #tTotal = tEnd - tStart + tTotal
        if len(argv) >= 3 :
            for l in labels :
                print l + " ->"
        correct = 0
        incorrect = 0
        for p in result :
            if result[p] > -0 :
                re = p.replace("'", "")
                re_str = re_str + re + ";"
                print re + ': ', result[p]
                if re in labels :
                    right_label = right_label + 1
                    correct = correct + 1
                    print 'yes'
                else :
                    incorrect = incorrect + 1
                    wrong_label = wrong_label + 1
                    print 'wrong'
        missed_label = missed_label + len(labels) - correct
        total_label = total_label + len(labels)
        if correct - incorrect == len(labels) :
            right_sent = right_sent + 1
            re_str = re_str + "\t" + "right"
            print "correct sent"
        else :
            re_str = re_str + "\t" + "wrong"
            print "incorrect sent"
        if correct == 0 and incorrect == 0 :
            uncovered_sent = uncovered_sent + 1
        
        print '========================'
        re_str = re_str + "\n"
        if len(labels) == 1 :
            fresult_data_one.write(re_str)
        else :
            fresult_data_multi.write(re_str)
        # erase input prolog file (sentence)
        fsent.truncate()
        fsent.close()
    fresult.write("total_sent:\t%d\ntotal_label:\t%d\nright_label:\t%d\nwrong_label:\t%d\nmissed_label:\t%d\ncorrect_sent:\t%d\nuncovered_sent:\t%d\n\ntotal_time:\t%f20\n" % (total_sent, total_label, right_label, wrong_label, missed_label, right_sent, uncovered_sent, tTotal))
    fresult.close()
    fresult_data_multi.close()
    fresult_data_one.close()
if __name__ == '__main__' :
    main(sys.argv[1:])